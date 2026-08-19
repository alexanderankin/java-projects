package info.ankin.projects.system.exec;

import info.ankin.projects.system.exec.Exec.ProcessConfig;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import java.io.ByteArrayOutputStream;
import java.nio.charset.StandardCharsets;
import java.util.List;

import static java.util.concurrent.TimeUnit.SECONDS;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.awaitility.Awaitility.await;

class ExecWaitForOutputTest {
    @ParameterizedTest
    @ValueSource(booleans = {false, true})
    void waitsUntilTheChildWritesReadyToEitherStandardStream(boolean writeToStandardError) {
        var standardOutput = new ByteArrayOutputStream();
        var standardError = new ByteArrayOutputStream();

        // Split "ready" across writes to show that matching does not depend on transfer chunk boundaries.
        var shellScript = "{ printf 'child is re'; sleep 0.1; printf 'ady'; }"
                + (writeToStandardError ? " >&2" : "");
        var runningProcess = new Exec().start(new ProcessConfig()
                .setCommand("sh")
                .setArguments(List.of("-c", shellScript))
                .streamStandardOutput(standardOutput)
                .streamStandardError(standardError));

        await().atMost(5, SECONDS).until(() ->
                standardOutput.toString(StandardCharsets.UTF_8).contains("ready")
                        || standardError.toString(StandardCharsets.UTF_8).contains("ready"));
        assertEquals(0, runningProcess.waitUntilCompletion().getExitCode());
    }
}
