package info.ankin.projects.cli.messagebus;

import org.junit.jupiter.api.Test;
import picocli.CommandLine;

import java.io.PrintWriter;
import java.io.StringWriter;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.containsString;

class MessageBusTest {

    @Test
    void hasBrokerTypeDescription() {
        String expected = "Type of broker API to use";
        StringWriter stringWriter = new StringWriter();
        PrintWriter pw = new PrintWriter(stringWriter);
        new CommandLine(MessageBus.class).setOut(pw).setErr(pw).execute("-h");
        String standardOut = stringWriter.toString();

        assertThat(standardOut, containsString(expected));
    }

    @Test
    void runsBaseCommand() {
        new CommandLine(MessageBus.class).execute();
    }

}
