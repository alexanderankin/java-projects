package info.ankin.projects.system.exec;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

class ExecTest {
    @ParameterizedTest
    @CsvSource({
            "'cat README.md'",
            "''",
    })
    void test() {

    }
}
