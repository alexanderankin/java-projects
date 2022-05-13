package info.ankin.projects.cli.killport;

import info.ankin.projects.infer_platform.Os;
import info.ankin.projects.infer_platform.PlatformInferrer;
import lombok.SneakyThrows;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.math.NumberUtils;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

public class KillPort {
    final List<Integer> ports;
    final Protocol method;
    final PlatformInferrer platformInferrer;

    /**
     * Parses inputs
     * <p>
     * currently defaults to TCP instead of parsing <code>--protocol</code> argument.
     *
     * @param args     inputs
     * @param inferrer helper to tell which platform we are running on
     */
    public KillPort(String[] args, PlatformInferrer inferrer) {
        ports = Arrays.stream(args)
                .filter(NumberUtils::isCreatable)
                .map(NumberUtils::createInteger)
                .collect(Collectors.toCollection(ArrayList::new));
        method = Protocol.TCP;
        this.platformInferrer = inferrer;
    }

    public static void main(String[] args) {
        // dependencies
        new KillPort(args, new PlatformInferrer()).run();
    }

    public void run() {
        System.out.println("hello, world!");
        Os os = platformInferrer.os();
        System.out.println("we are running on " + os + "!");
        System.out.println("we are removing processes listening on ports: " + ports);
        run(os);
    }

    @SneakyThrows
    private void run(Os os) {
        switch (os) {
            case WINDOWS:
                String output = execNetstatWindows();
                for (Integer port : ports) {
                    Set<Integer> pidSet = parseWindowsOutput(output, method, port);
                    if (pidSet.isEmpty()) continue;

                    List<String> pidList = pidSet.stream()
                            .map(Object::toString)
                            .collect(Collectors.toList());

                    StringJoiner joiner = new StringJoiner(" /PID ", "/PID ", "");
                    pidList.forEach(joiner::add);
                    String pidFlags = joiner.toString();
                    exec("TaskKill /F " + pidFlags);
                }
                break;
            case LINUX:
            case DARWIN:
                String interfaceName = method.lsofInterfaceName();
                String nameFdMode = method.lsofOutputNameColumnFdMode();
                for (Integer port : ports) {
                    exec("lsof -ni " + interfaceName + ":" + port + " | grep " + nameFdMode + " | awk '{print $2}' | xargs kill -9");
                }
                break;
            default:
                throw new UnsupportedOperationException("Not supported yet: " + os + " (" + System.getProperty("os.name") + ")");
        }
    }

    Set<Integer> parseWindowsOutput(String output, Protocol method, Integer port) {
        // whitespace, followed by: non-ws:$port, anything, ws, PID
        Pattern pidFinder = Pattern.compile("^ *" + method + " *[^ ]*:" + port + ".* +(\\d+)", Pattern.MULTILINE);
        Matcher matcher = pidFinder.matcher(output);
        return matcher.results().map(e -> e.group(1)).filter(NumberUtils::isCreatable).map(NumberUtils::createInteger).collect(Collectors.toSet());
    }

    String execNetstatWindows() throws IOException, InterruptedException {
        return exec("netstat -nao");
    }

    String exec(String command) throws IOException, InterruptedException {
        ProcessBuilder processBuilder = new ProcessBuilder();
        String shell = platformInferrer.os() == Os.WINDOWS ? "cmd" : "sh";
        String flag = platformInferrer.os() == Os.WINDOWS ? "/C" : "-c";
        processBuilder.command(shell, flag, command);
        Process process = processBuilder.start();
        InputStream output = process.getInputStream();
        int exitCode = process.waitFor();
        System.out.println("process exited with code: " + exitCode);
        return IOUtils.toString(output, StandardCharsets.UTF_8);
    }

    enum Protocol {
        TCP {
            @Override
            String lsofOutputNameColumnFdMode() {
                return "LISTEN";
            }
        },
        UDP,
        ;

        String lsofInterfaceName() {
            return name().toLowerCase();
        }

        String lsofOutputNameColumnFdMode() {
            return name();
        }
    }
}
