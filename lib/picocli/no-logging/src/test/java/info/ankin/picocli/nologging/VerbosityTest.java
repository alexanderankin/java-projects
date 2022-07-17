package info.ankin.picocli.nologging;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class VerbosityTest {

    @Test
    void test() {
        assertEquals(0, Verbosity.verbosity());
        assertEquals(1, Verbosity.verbosity("-v"));
        assertEquals(2, Verbosity.verbosity("-vv"));
        assertEquals(2, Verbosity.verbosity("-v", "-v"));
        assertEquals(3, Verbosity.verbosity("-v", "-vv"));
        assertEquals(3, Verbosity.verbosity("-vvv"));
        assertEquals(3, Verbosity.verbosity("-vv", "--verbose"));
    }

}
