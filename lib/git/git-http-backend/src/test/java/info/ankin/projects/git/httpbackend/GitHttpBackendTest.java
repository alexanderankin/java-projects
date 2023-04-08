package info.ankin.projects.git.httpbackend;

import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.file.PathUtils;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import java.nio.file.FileSystem;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;

@Slf4j
class GitHttpBackendTest extends BaseTest {

    @TempDir
    Path path;

    @CsvSource({
            "/abc/def,false",
            "/abc/def/./,true",
            "/abc/def/./efg,true",
            "/abc/def/../efg,true",
            "../,true",
    })
    @ParameterizedTest
    void test_detectsRelativeOrParent(String input, boolean expected) {
        GitHttpBackend gitHttpBackend = new GitHttpBackend();
        assertEquals(expected, gitHttpBackend.hasRelativeOrParent(input));
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
