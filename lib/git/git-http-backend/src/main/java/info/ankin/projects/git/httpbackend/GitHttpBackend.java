package info.ankin.projects.git.httpbackend;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.SneakyThrows;
import lombok.experimental.Accessors;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.reactive.function.client.ClientRequest;
import org.springframework.web.reactive.function.client.ClientResponse;
import reactor.core.publisher.Mono;

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
        throw new UnsupportedOperationException();
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
