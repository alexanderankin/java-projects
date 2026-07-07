package info.ankin.projects.system.exec;

import info.ankin.projects.system.exec.Exec.ProcessConfig;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import java.io.ByteArrayOutputStream;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class ExecTest {
    Exec exec;

    @BeforeEach
    void setUp() {
        exec = new Exec();
    }

    @ParameterizedTest
    @CsvSource({
            "'cat README.md'",
            "''",
    })
    void test() {

    }

    @Test
    void test_basic() {
        var completedProcess = exec.run(new ProcessConfig()
                .setCaptureOutput(true)
                .setStandardOutputCharset(StandardCharsets.UTF_8)
                .setCommand("whoami"));
        // System.out.println(completedProcess);
        assertTrue(completedProcess.getStandardOutputString().contains(System.getProperty("user.name")));


        exec.run(new ProcessConfig().setCommand("whoami").setStandardOutput(ProcessConfig.ProcessStream.DiscardProcessStream.INSTANCE));
        System.out.println(Arrays.toString(exec.run(new ProcessConfig().setCommand("whoami").setStandardOutput(ProcessConfig.ProcessStream.DiscardProcessStream.INSTANCE).setCaptureOutput(true)).getStandardOutput()));
    }

    @Test
    void longRunningTest() {
        var outputStream = new ByteArrayOutputStream();
        var bash = assertThrows(Exec.ExecExitCodeException.class, () -> exec.run(new ProcessConfig()
                .setCommand("bash")
                .setArguments(List.of("-c", "for i in {1..10000}; do printf $i; sleep 1; done"))
                .setStandardOutput(new ProcessConfig.ProcessStream.PipeProcessStream.ProcessOutputToStream().setOutputStream(outputStream))
                .setTimeout(Duration.ofSeconds(5))
                .setTimeoutAction(ProcessConfig.TimeoutAction.SigTermThenThrow.DEFAULT)));
        System.out.println(outputStream.size());
        System.out.println(bash.completedProcess);
    }
}
