package info.ankin.projects.picocli.logback.verbosity;

import ch.qos.logback.classic.Level;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import static org.junit.jupiter.api.Assertions.assertEquals;

class LogbackVerbosityMixinTest {
    LogbackVerbosityMixin mixin;
    Level previousLevel;

    @BeforeEach
    void setup() {
        mixin = new LogbackVerbosityMixin();
        previousLevel = mixin.rootLogger.getLevel();
    }

    @AfterEach
    void teardown() {
        mixin.rootLogger.setLevel(previousLevel);
    }

    @Test
    void testVerbosity() {
        assertEquals(Level.WARN.levelInt, mixin.rootLogger.getLevel().levelInt);
        mixin.verbosity(new boolean[]{true});
        assertEquals(Level.INFO.levelInt, mixin.rootLogger.getLevel().levelInt);
        mixin.verbosity(new boolean[]{});
        assertEquals(Level.WARN.levelInt, mixin.rootLogger.getLevel().levelInt);
        mixin.verbosity(new boolean[]{false});
        assertEquals(Level.ERROR.levelInt, mixin.rootLogger.getLevel().levelInt);
    }

    @ParameterizedTest
    @CsvSource({
            "-10,OFF",
            "-3,OFF",
            "-2,OFF",
            "-1,ERROR",
            "0,WARN",
            "1,INFO",
            "2,DEBUG",
            "3,TRACE",
            "10,TRACE",
    })
    void testInts(int verbosity, String expectedLevel) {
        mixin.setVerbosity(verbosity);
        assertEquals(Level.toLevel(expectedLevel).levelInt, mixin.rootLogger.getLevel().levelInt);
    }
}
