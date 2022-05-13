package info.ankin.projects.cli.killport;

import info.ankin.projects.infer_platform.Os;
import info.ankin.projects.infer_platform.PlatformInferrer;
import lombok.SneakyThrows;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.tuple.Pair;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.util.ArrayDeque;
import java.util.Deque;
import java.util.List;
import java.util.Objects;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class KillPortTest {

    private static final Path path = Path.of("/info/ankin/projects/cli/killport");

    KillPort killPort;
    PlatformInferrer platformInferrer;
    Deque<String> execArgs = new ArrayDeque<>();
    Deque<String> execResults = new ArrayDeque<>();
    Deque<String> execNetstatWinResults = new ArrayDeque<>();

    @BeforeEach
    void setUp() {
        platformInferrer = mock(PlatformInferrer.class);
        killPort = new KillPort(new String[]{}, platformInferrer) {
            final Deque<String> execResults = KillPortTest.this.execResults;
            final Deque<String> execNetstatWinResults = KillPortTest.this.execNetstatWinResults;

            @Override
            String execNetstatWindows() {
                return execNetstatWinResults.pop();
            }

            @Override
            protected String exec(String s) {
                execArgs.push(s);
                return execResults.isEmpty() ? null : execResults.pop();
            }
        };
    }

    @SneakyThrows
    String getSampleInput() {
        return IOUtils.toString(Objects.requireNonNull(getClass().getResourceAsStream(path.resolve("example-netstat-windows-output.txt").toString())), StandardCharsets.UTF_8);
    }

    @Test
    void test_parsingWindowsOutput() {
        String output = getSampleInput();

        assertThat(killPort.parseWindowsOutput(output, KillPort.Protocol.TCP, 49155), contains(432));
        assertThat(killPort.parseWindowsOutput(output, KillPort.Protocol.TCP, 445), contains(4));
        assertThat(killPort.parseWindowsOutput(output, KillPort.Protocol.TCP, 446), empty());
    }


    @Test
    void test_win() {
        when(platformInferrer.os()).thenReturn(Os.WINDOWS);
        String output = getSampleInput();

        List<Pair<Integer, Integer>> ports = List.of(Pair.of(49155, 432), Pair.of(445, 4), Pair.of(446, null));

        for (Pair<Integer, Integer> pair : ports) {
            Integer port = pair.getLeft();
            killPort.ports.clear();
            killPort.ports.add(port);
            execNetstatWinResults.push(output);
            killPort.run();
            if (pair.getRight() == null) {
                assertThat(execArgs, empty());
            } else {
                String result = execArgs.pop();
                assertThat(result, is("TaskKill /F /PID " + pair.getRight()));
            }
        }
    }

    @Test
    void test_linux() {
        when(platformInferrer.os()).thenReturn(Os.LINUX);
        killPort.ports.clear();
        killPort.ports.add(8080);
        killPort.run();
        String args = execArgs.pop();
        assertThat(args, is("lsof -ni tcp:8080 | grep LISTEN | awk '{print $2}' | xargs kill -9"));
    }

    @Test
    void test_unknown() {
        when(platformInferrer.os()).thenReturn(Os.UNKNOWN);
        UnsupportedOperationException uoe =
                assertThrows(UnsupportedOperationException.class, killPort::run);
        String actual = System.getProperty("os.name");
        assertThat(uoe.getMessage(), is("Not supported yet: UNKNOWN (" + actual + ")"));

    }

}
