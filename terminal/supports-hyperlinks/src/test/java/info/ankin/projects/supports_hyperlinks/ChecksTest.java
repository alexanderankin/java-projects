package info.ankin.projects.supports_hyperlinks;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.util.Map;

class ChecksTest {

    @BeforeAll
    static void setUp() {
        Check.Checks.getEnv = Map.<String, String>of()::get;
    }

    @AfterAll
    static void tearDown() {
        Check.Checks.getEnv = System::getenv;
    }

    @Test
    void test() {
        Check.Checks.VALUES.stream().map(Check::check).forEach(Assertions::assertFalse);
    }
}
