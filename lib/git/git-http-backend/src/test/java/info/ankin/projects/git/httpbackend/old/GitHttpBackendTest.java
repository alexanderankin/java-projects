package info.ankin.projects.git.httpbackend.old;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import static org.junit.jupiter.api.Assertions.assertEquals;

class GitHttpBackendTest {

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

}
