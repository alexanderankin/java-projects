package info.ankin.projects.git.httpbackend;

import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.file.PathUtils;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.springframework.http.HttpMethod;
import org.springframework.web.reactive.function.client.ClientRequest;

import java.net.URI;
import java.nio.file.FileSystem;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.stream.Stream;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;

@Slf4j
class GitHttpBackendTest extends BaseTest {

    GitHttpBackend gitHttpBackend;

    @TempDir
    Path path;

    @BeforeEach
    void setup() {
        gitHttpBackend = new GitHttpBackend(new GitHttpBackend.Config());
    }

    @CsvSource({
            "/abc, false",
            "/repo/info/refs, true",
            "/repo/HEAD, true",
            "/objects/info/refs, true",
            "/objects/ab/aaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaa, false",
            "/objects/ab/aaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaa, true",
            "/objects/ab/aaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaa/other, false",
            "/objects/pack/pack-aaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaa, false",
            "/git-upload-pack, true",
            "/repo/git-upload-pack, true",
            "/repo/git-upload-pack/other, false",
            "/git-receive-pack, true",
            "/repo/git-receive-pack, true",
            "/repo/git-receive-pack/other, false",
    })
    @ParameterizedTest
    void test_supportedRequestRegex(String url, boolean expectedValue) {
        assertThat(gitHttpBackend.test(request(url)), is(expectedValue));
    }

    private ClientRequest request(String path) {
        return ClientRequest.create(HttpMethod.GET, URI.create(path)).build();
    }

    // @Disabled("to show usage of tmp-dir based git repo")
    @SneakyThrows
    @Test
    void test_unzippingGitZip() {
        try (FileSystem zipFs = newFileSystemFromResource("/samples/sample-git-repository.zip")) {
            Path zipDir = zipFs.getPath("/");
            PathUtils.copyDirectory(zipDir, path);
            if (log.isDebugEnabled())
                try (Stream<Path> walk = Files.walk(zipDir)) {
                    walk.forEach(System.out::println);
                }
        }
    }
}
