package info.ankin.projects.git.httpbackend;

import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.codec.binary.Hex;
import org.apache.commons.io.file.PathUtils;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import org.springframework.web.reactive.function.client.WebClient;

import java.net.URI;
import java.nio.file.FileSystem;
import java.nio.file.Files;
import java.nio.file.Path;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;

@Slf4j
class GitSimpleHttpServerTest extends BaseTest {

    @TempDir
    static Path path;
    static GitSimpleHttpServer gitSimpleHttpServer;
    static WebClient webClient;

    @SneakyThrows
    @BeforeAll
    static void beforeAll() {
        try (FileSystem zipFs = new BaseTest().newFileSystemFromResource("/samples/sample-git-repository.zip")) {
            Path zipDir = zipFs.getPath("/");
            PathUtils.copyDirectory(zipDir, path);
        }
        gitSimpleHttpServer = new GitSimpleHttpServer(path.resolve("sample-git-repository").toString(), null);
        gitSimpleHttpServer.start();
        webClient = WebClient.builder()
                .baseUrl(new URI("http",
                        null,
                        gitSimpleHttpServer.getDisposableServer().host(),
                        gitSimpleHttpServer.getDisposableServer().port(),
                        "/",
                        null,
                        null).toString())
                .build();
    }

    @AfterAll
    static void afterAll() {
        path = null;
        gitSimpleHttpServer.stop();
        gitSimpleHttpServer = null;
        webClient = null;
    }

    @SneakyThrows
    @Test
    void test_regeneratesInfoRefs() {
        Path infoRefsPath = Path.of(gitSimpleHttpServer.getRepoPath(), ".git", "info", "refs");
        Files.deleteIfExists(infoRefsPath);

        String infoRefs = webClient.get().uri("/info/refs").retrieve().bodyToMono(String.class).toFuture().join();
        assertThat(infoRefs, is("c999159697ea2f9ca895d8884ad51417d8014324\trefs/heads/main\n"));

        // testing a little bit of implementation, as a treat
        assertThat(Files.isReadable(infoRefsPath), is(true));
    }

    @Test
    void test_object() {
        String object = webClient.get().uri("/objects/c9/99159697ea2f9ca895d8884ad51417d8014324")
                .retrieve().bodyToMono(byte[].class).map(Hex::encodeHexString)
                .toFuture().join();
        assertThat(object,
                is("780185cc410ac230104051d739c57880c22499a6" +
                        "191071271e2349a71ab016e2f4fe967a80eefffb" +
                        "6599e7aa60894eda44a0241fa75128b3642144eb" +
                        "23170e7608e27a4f81c5e58c636fd2aaafa5c17d" +
                        "c397f52bedf6ac7a051b227244c70374b87153f6" +
                        "bfca71691e9faa35bde14fcee607dfee2d00"));
    }

    @Test
    void test_HEAD() {
        String head = webClient.get().uri("/HEAD").retrieve().bodyToMono(String.class).toFuture().join();
        assertThat(head, is("ref: refs/heads/main\n"));
    }

}
