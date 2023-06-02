package info.ankin.projects.git.httpbackend;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.SneakyThrows;
import lombok.experimental.Accessors;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.core.io.buffer.DefaultDataBuffer;
import org.springframework.core.io.buffer.DefaultDataBufferFactory;
import org.springframework.http.MediaType;
import org.springframework.web.reactive.function.client.ClientRequest;
import org.springframework.web.reactive.function.client.ClientResponse;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Flux;
import reactor.core.publisher.FluxSink;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URI;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Optional;
import java.util.concurrent.TimeUnit;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.regex.Pattern;

/**
 * stand-alone server based on {@code git http-backend}.
 *
 * @see <a href="https://man7.org/linux/man-pages/man1/git-http-backend.1.html">git http-backend manual page</a>
 */
@SuppressWarnings("unused")
@Slf4j
@AllArgsConstructor
public class GitHttpBackend
        implements Function<ClientRequest, Mono<ClientResponse>>, Predicate<ClientRequest> {
    /**
     * objects we understand = info/*, xx/yyy..., pack/pack-zzz...{.(pack|idx)}
     */
    static final Pattern IS_OBJECTS_URL =
            Pattern.compile("objects/(info/[^/]+|" +
                    "[0-9a-f]{2}/[0-9a-f]{38}|" +
                    "pack/pack-[0-9a-f]{40}\\.(pack|idx))$");
    static final Pattern IS_GIT_UPLOAD_OR_RECEIVE_PACK_URL =
            Pattern.compile("git-(upload|receive)-pack$");

    /**
     * takes precedence over header 'git-protocol'
     * <hr />
     * <p>
     * This is not strictly necessary using Apache and a modern version of
     * git-http-backend, as the webserver will pass along the header in the
     * environment as HTTP_GIT_PROTOCOL, and http-backend will copy that into
     * GIT_PROTOCOL. But you may need this line (or something similar if you
     * are using a different webserver), or if you want to support older Git
     * versions that did not do that copying.
     * <p>
     * Having the webserver set up GIT_PROTOCOL is perfectly fine even with
     * modern versions (and will take precedence over HTTP_GIT_PROTOCOL,
     * which means it can be used to override the client's request).
     * <p>
     * e.g.:
     * <pre>SetEnvIf Git-Protocol ".*" GIT_PROTOCOL=$0</pre>
     */
    static final String ENV_VAR_GIT_PROTOCOL = "GIT_PROTOCOL";
    static final String ENV_VAR_GIT_HTTP_EXPORT_ALL = "GIT_HTTP_EXPORT_ALL";
    // http.maxRequestBuffer, e.g. 10M (for 10 mb)
    static final String ENV_VAR_GIT_HTTP_MAX_REQUEST_BUFFER = "GIT_HTTP_MAX_REQUEST_BUFFER";
    // uri vars
    static final String ENV_VAR_URI_PATH_INFO = "PATH_INFO";
    static final String ENV_VAR_URI_GIT_PROJECT_ROOT = "GIT_PROJECT_ROOT";
    static final String ENV_VAR_URI_PATH_TRANSLATED = "PATH_TRANSLATED";
    // cgi vars
    static final String CGI_REQUEST_METHOD = "REQUEST_METHOD";

    private final Config config;
    private final String ghbBinary;

    public GitHttpBackend(Config config) {
        this.config = config;
        this.ghbBinary = config.ghb();
    }

    public Mono<ClientResponse> apply(ClientRequest clientRequest) {
        // map.put("PATH_INFO", pathInfo);
        // map.put("REMOTE_USER", remoteUser);
        // map.put("REMOTE_ADDR", remoteAddr);
        // map.put("CONTENT_TYPE", contentType);
        // map.put("QUERY_STRING", queryString);
        // map.put("REQUEST_METHOD", requestMethod);
        return Mono.fromCallable(() -> doApply(clientRequest));
    }

    private ClientResponse doApply(ClientRequest clientRequest) {
        // return ClientResponse.create(HttpStatus.OK)
        //         .body(Flux.from)
        //         .build()
        throw new UnsupportedOperationException();
    }

    // https://stackoverflow.com/a/49549721
    private Mono<ServerResponse> writeToServerResponse(InputStream inputStream) {
        // final long blobSize = tag.getBlobSize();
        long blobSize = 1;
        long tagChunkSize = 1;
        return ServerResponse.ok()
                .contentType(MediaType.APPLICATION_OCTET_STREAM)
                .body(Flux.create(emitter -> {
                                    // for a huge blob I want to read it in chunks, so that my server doesn't use too much memory
                                    for (int i = 0; i < blobSize; i += tagChunkSize) {
                                        // new DataBuffer that is written to, then emitted later
                                        DefaultDataBuffer dataBuffer = new DefaultDataBufferFactory().allocateBuffer();
                                        try (OutputStream outputStream = dataBuffer.asOutputStream()) {
                                            // write to the output stream of DataBuffer
                                            // tag.BlobReadPartial(outputStream, i, tagChunkSize, FPLibraryConstants.FP_OPTION_DEFAULT_OPTIONS);
                                            // don't know if flushing is strictly necessary
                                            outputStream.flush();
                                        } catch (IOException e) {
                                            log.error("Error reading + writing from tag to http outputstream", e);
                                            emitter.error(e);
                                        }
                                        emitter.next(dataBuffer);
                                    }
                                    // if blob is finished, send "complete" to my flux of DataBuffers
                                    emitter.complete();
                                }, FluxSink.OverflowStrategy.BUFFER)
                                .publishOn(Schedulers.boundedElastic())
                        ,
                        DataBuffer.class);
    }

    /**
     * @return whether git-http-backend may support the request
     * @see <a href="https://man7.org/linux/man-pages/man1/git-http-backend.1.html#EXAMPLES">ghp man-pages</a>
     */
    public boolean test(ClientRequest clientRequest) {
        String path = path(clientRequest);

        if (path.endsWith("/HEAD") || path.endsWith("/info/refs"))
            return true;

        if (IS_OBJECTS_URL.matcher(path).find()) return true;

        return IS_GIT_UPLOAD_OR_RECEIVE_PACK_URL.matcher(path).find();
    }

    private String path(ClientRequest clientRequest) {
        URI url = clientRequest.url();

        String path = url.getPath();
        String query = Optional.ofNullable(url.getQuery()).map("?"::concat).orElse("");
        String fragment = Optional.ofNullable(url.getFragment()).map("#"::concat).orElse("");

        return path + query + fragment;
    }

    @Accessors(chain = true)
    @Data
    public static class Config {
        /**
         * git-http-backend binary location
         */
        String ghbBinary;

        /**
         * by default, detect it from git in the path
         */
        boolean detectGhbBinary = true;

        /**
         *
         */
        String projectsRoot;

        /**
         * sets the {@code GIT_HTTP_EXPORT_ALL} so that
         * the {@code git-daemon-export-ok} file is not required.
         */
        boolean forceExportAll;

        @SneakyThrows
        String ghb() {
            if (ghbBinary != null && Files.exists(Path.of(ghbBinary))) return ghbBinary;
            if (detectGhbBinary) {
                byte[] stdOut = new ProcessBuilder()
                        // https://stackoverflow.com/a/42008526
                        .command("git", "--exec-path")
                        .redirectOutput(ProcessBuilder.Redirect.PIPE)
                        .start()
                        .onExit().get(5, TimeUnit.SECONDS)
                        .getInputStream().readAllBytes();

                String git = new String(stdOut, StandardCharsets.UTF_8).trim();
                Path ghbPath = Path.of(git, "git-http-backend");
                if (Files.exists(ghbPath))
                    return ghbPath.toString();
            }

            throw new IllegalStateException("could not detect git-http-backend binary");
        }
    }
}
