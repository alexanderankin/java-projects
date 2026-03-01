package info.ankin.projects.cli.yaml2json;

import lombok.SneakyThrows;
import org.junit.jupiter.api.Test;

import java.io.*;
import java.nio.charset.StandardCharsets;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;

public class ConversionTest {
    @SneakyThrows
    @Test
    void test_yaml2Json() {
        try (var redirector = new ByteArrayStandardStreamsRedirector()) {
            try (var pw = redirector.tempInPrinter()) {
                pw.write("a: true");
            }
            Yaml2Json.main("".split(""));
            var output = redirector.tempOut.toString(StandardCharsets.UTF_8);
            assertThat(output, is("{\"a\":true}\n"));
        }
    }

    @SneakyThrows
    @Test
    void test_json2Yaml() {
        try (var redirector = new ByteArrayStandardStreamsRedirector()) {
            try (var pw = redirector.tempInPrinter()) {
                pw.write("{\"a\": true}");
            }
            Json2Yaml.main("".split(""));
            var output = redirector.tempOut.toString(StandardCharsets.UTF_8);
            assertThat(output, is("---\na: true\n\n"));
        }
    }

    static class ByteArrayStandardStreamsRedirector implements AutoCloseable {
        final InputStream originalIn;
        final PrintStream originalOut;
        final PrintStream originalErr;
        final PipedInputStream tempIn;
        final ByteArrayOutputStream tempOut;
        final ByteArrayOutputStream tempErr;

        ByteArrayStandardStreamsRedirector() {
            originalIn = System.in;
            originalOut = System.out;
            originalErr = System.err;
            tempIn = new PipedInputStream();
            System.setIn(tempIn);
            tempOut = new ByteArrayOutputStream();
            System.setOut(new PrintStream(tempOut, true));
            tempErr = new ByteArrayOutputStream();
            System.setErr(new PrintStream(tempErr, true));
        }

        @SneakyThrows
        PrintWriter tempInPrinter() {
            var src = new PipedOutputStream();
            tempIn.connect(src);
            return new PrintWriter(src, true);
        }

        @Override
        public void close() {
            System.setIn(originalIn);
            System.setOut(originalOut);
            System.setErr(originalErr);
        }
    }
}
