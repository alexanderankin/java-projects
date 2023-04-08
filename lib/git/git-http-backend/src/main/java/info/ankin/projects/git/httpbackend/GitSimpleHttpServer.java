package info.ankin.projects.git.httpbackend;

import lombok.AccessLevel;
import lombok.Data;
import lombok.Getter;
import lombok.SneakyThrows;
import lombok.experimental.Tolerate;
import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;
import reactor.netty.DisposableServer;
import reactor.netty.http.server.HttpServer;

import java.nio.file.Path;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.concurrent.atomic.AtomicReference;

/**
 * implementing the simple version of the protocol
 *
 * @see <a href="https://blog.thesparktree.com/git-mirror-anywhere-using-dumb-http-protocol">thesparktree</a>
 * @see <a href="https://www.git-scm.com/docs/http-protocol">Git SCM docs</a>
 * @see <a href="https://git-scm.com/book/en/v2/Git-on-the-Server-The-Protocols">Git SCM Book (v2)</a>
 */
@Slf4j
@Data
public class GitSimpleHttpServer {
    private static final Set<String> ALLOWED_URLS = Set.of("info/refs", "HEAD");
    private static final List<String> ALLOWED_STARTS_WITH_URLS = List.of("objects/");
    private static final Set<String> RERUN_FOR_URLS = Set.of("info/refs");

    final String repoPath;
    final Integer port;
    DisposableServer disposableServer;

    @Getter(AccessLevel.NONE)
    private final AtomicReference<Mono<Integer>> rerunMono = new AtomicReference<>();
    @Getter(AccessLevel.NONE)
    private Path repoPathPath;
    @Getter(AccessLevel.NONE)
    private String normalized;

    @Tolerate
    public GitSimpleHttpServer(String repoPath) {
        this.repoPath = repoPath;
        port = 8080;
    }

    @SneakyThrows
    public void start() {
        if (disposableServer != null) {
            log.warn("starting GitSimpleHttpServer, but it's already started");
            return;
        }

        HttpServer httpServer = HttpServer.create();

        if (port != null) httpServer = httpServer.port(port);

        httpServer = httpServer.handle((req, res) -> {
                    String path = req.path();
                    if (ALLOWED_URLS.contains(path) ||
                            // if path starts with any known good prefix
                            ALLOWED_STARTS_WITH_URLS.stream().anyMatch(path::startsWith)) {
                        // if (req.path().equals("info/refs") || req.path().equals("HEAD") || req.path().startsWith("objects/")) {
                        Path repoPath = repoPath();
                        repoPath = repoPath.resolve(".git");
                        repoPath = repoPath.resolve(path);
                        repoPath = repoPath.normalize();

                        if (!repoPath.toString().startsWith(normalized())) {
                            return res.status(401).send();
                        }

                        var sentFile = repoPath;

                        Mono<Void> prepWork = RERUN_FOR_URLS.contains(path) ? reRunUSI().then() : Mono.empty();

                        return prepWork.then(Mono.defer(() -> Mono.from(res.sendFile(sentFile))));
                    } else {
                        return res.status(404).send();
                    }
                });

        disposableServer = httpServer.bindNow();
    }

    public void stop() {
        if (disposableServer != null) disposableServer.disposeNow();
    }

    private Mono<Integer> reRunUSI() {
        Mono<Integer> mono = Mono.fromCallable(() -> new ProcessBuilder()
                        .command("git", "-C", repoPath, "update-server-info")
                        .start()
                        .waitFor())
                .subscribeOn(Schedulers.boundedElastic());

        boolean changed = rerunMono.compareAndSet(null, mono);
        return changed
                ? mono // if we changed, use the new value
                // if we are here, it was very recently not null
                : Objects.requireNonNullElseGet(rerunMono.get(), Mono::empty);
    }

    private Path repoPath() {
        if (repoPathPath == null) repoPathPath = Path.of(repoPath).normalize();
        return repoPathPath;
    }

    private String normalized() {
        if (normalized == null) normalized = repoPath().toString();
        return normalized;
    }
}
