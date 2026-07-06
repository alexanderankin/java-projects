package info.ankin.projects.system.exec;

import lombok.*;
import lombok.experimental.Accessors;
import lombok.extern.slf4j.Slf4j;

import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.Path;
import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.TimeUnit;

@Slf4j
public class Exec {
    public CompletedProcess run(ProcessConfig processConfig) {
        return start(processConfig).waitUntilCompletion();
    }

    @SneakyThrows
    public RunningProcess start(ProcessConfig processConfig) {
        var pb = new ProcessBuilder();

        var workingDirectory = processConfig.getWorkingDirectory();
        if (workingDirectory != null)
            pb.directory(workingDirectory.toFile());

        var command = processConfig.getCommand();
        Objects.requireNonNull(command, "must have command to run process");
        List<String> pbCommand;
        var arguments = processConfig.getArguments();
        if (arguments == null) {
            pbCommand = List.of(command);
        } else {
            pbCommand = new ArrayList<>(1 + arguments.size());
            pbCommand.add(command);
            pbCommand.addAll(arguments);
        }
        pb.command(pbCommand);

        var environment = processConfig.getEnvironment();
        if (environment != null) {
            var pbEnvironment = pb.environment();
            pbEnvironment.clear();
            pbEnvironment.putAll(environment);
        }

        pb.redirectInput(processConfig.standardInput.getMode().redirect);

        var captureOutput = processConfig.isCaptureOutput();
        if (captureOutput) {
            pb.redirectOutput(ProcessBuilder.Redirect.PIPE);
            pb.redirectError(ProcessBuilder.Redirect.PIPE);
        } else {
            pb.redirectOutput(processConfig.standardOutput.getMode().redirect);
            pb.redirectError(processConfig.standardError.getMode().redirect);
        }

        var process = pb.start();

        if (captureOutput) {
        }

        return new RunningProcess()
                .setConfig(processConfig)
                .setProcess(process);
    }

    @ToString(callSuper = true)
    @EqualsAndHashCode(callSuper = true)
    @Data
    @Accessors(chain = true)
    public static class CompletedProcess extends RunningProcess {
        int exitCode;
        byte[] standardOutput;
        byte[] standardError;
    }

    @Data
    @Accessors(chain = true)
    public static class RunningProcess {
        ProcessConfig config;
        Process process;

        @SneakyThrows
        public CompletedProcess waitUntilCompletion() {
            if (config.timeout == null) {
                process.waitFor();
            } else {
                if (!process.waitFor(TimeUnit.NANOSECONDS.convert(config.timeout), TimeUnit.NANOSECONDS)) {
                    switch (config.timeoutAction) {
                        case ProcessConfig.TimeoutAction.No ignored -> {
                        }
                        case ProcessConfig.TimeoutAction.Throw ignored ->
                                throw new ExecTimeoutException().setProcessConfig(config);
                        case ProcessConfig.TimeoutAction.SigTermThenSigKillThenThrow termKillThrow -> {
                            process.destroy();
                            if (!process.waitFor(TimeUnit.NANOSECONDS.convert(termKillThrow.timeoutAfterSigTerm), TimeUnit.NANOSECONDS)) {
                                process.destroyForcibly();
                                if (!process.waitFor(TimeUnit.NANOSECONDS.convert(termKillThrow.timeoutAfterSigKill), TimeUnit.NANOSECONDS)) {
                                    throw new ExecTimeoutException().setProcessConfig(config);
                                }
                            }
                        }
                        case ProcessConfig.TimeoutAction.SigTermThenThrow termThenThrow -> {
                            if (!process.waitFor(TimeUnit.NANOSECONDS.convert(termThenThrow.timeoutAfterSigTerm), TimeUnit.NANOSECONDS)) {
                                throw new ExecTimeoutException().setProcessConfig(config);
                            }
                        }
                    }
                }
            }

            var completedProcess = new CompletedProcess();
            completedProcess
                    .setConfig(config)
                    .setProcess(process);
            return completedProcess.setExitCode(process.exitValue());
        }
    }

    @Data
    @Accessors(chain = true)
    public static class ProcessConfig {
        Path workingDirectory;
        String command;
        List<String> arguments;
        Map<String, String> environment;
        boolean captureOutput;
        ProcessStream.FromStream standardInput = ProcessStream.InheritedProcessStream.INSTANCE;
        ProcessStream.ToStream standardOutput = ProcessStream.InheritedProcessStream.INSTANCE;
        ProcessStream.ToStream standardError = ProcessStream.InheritedProcessStream.INSTANCE;
        Duration timeout;
        TimeoutAction timeoutAction = new TimeoutAction.SigTermThenSigKillThenThrow();

        //<editor-fold desc="stream helper setters">
        public ProcessConfig inheritStandardInput() {
            standardInput = ProcessStream.InheritedProcessStream.INSTANCE;
            return this;
        }

        public ProcessConfig inheritStandardOutput() {
            standardOutput = ProcessStream.InheritedProcessStream.INSTANCE;
            return this;
        }

        public ProcessConfig inheritStandardError() {
            standardError = ProcessStream.InheritedProcessStream.INSTANCE;
            return this;
        }

        public ProcessConfig streamStandardInput(OutputStream stream) {
            standardInput = new ProcessStream.PipeProcessStream.PipeProcessStream.ProcessInputFromStream().setOutputStream(stream);
            return this;
        }

        public ProcessConfig streamStandardOutput(InputStream stream) {
            standardOutput = new ProcessStream.PipeProcessStream.PipeProcessStream.ProcessOutputToStream().setInputStream(stream);
            return this;
        }

        public ProcessConfig streamStandardError(InputStream stream) {
            standardError = new ProcessStream.PipeProcessStream.PipeProcessStream.ProcessOutputToStream().setInputStream(stream);
            return this;
        }
        //</editor-fold>

        public sealed interface ProcessStream {
            Mode getMode();

            @RequiredArgsConstructor
            public enum Mode {
                discard(ProcessBuilder.Redirect.DISCARD),
                inherit(ProcessBuilder.Redirect.INHERIT),
                pipe(ProcessBuilder.Redirect.PIPE),
                ;

                final ProcessBuilder.Redirect redirect;
            }

            sealed interface FromStream extends ProcessStream {
            }

            sealed interface ToStream extends ProcessStream {
            }

            @Data
            @Accessors(chain = true)
            public static final class InheritedProcessStream implements ProcessStream, FromStream, ToStream {
                public static final InheritedProcessStream INSTANCE = new InheritedProcessStream();
                public final Mode mode = Mode.inherit;
            }

            @Data
            @Accessors(chain = true)
            public static final class DiscardProcessStream implements ProcessStream, FromStream, ToStream {
                public static final DiscardProcessStream INSTANCE = new DiscardProcessStream();
                public final Mode mode = Mode.discard;
            }

            @Data
            @Accessors(chain = true)
            public static abstract sealed class PipeProcessStream implements ProcessStream {
                public final Mode mode = Mode.pipe;

                @ToString(callSuper = true)
                @EqualsAndHashCode(callSuper = true)
                @Data
                @Accessors(chain = true)
                public static final class FilePipeProcessStream extends PipeProcessStream implements FromStream, ToStream {
                    Path file;
                }

                @ToString(callSuper = true)
                @EqualsAndHashCode(callSuper = true)
                @Data
                @Accessors(chain = true)
                public static final class ProcessInputFromStream extends PipeProcessStream implements FromStream {
                    OutputStream outputStream;
                }

                @ToString(callSuper = true)
                @EqualsAndHashCode(callSuper = true)
                @Data
                @Accessors(chain = true)
                public static final class ProcessOutputToStream extends PipeProcessStream implements ToStream {
                    InputStream inputStream;
                }
            }

        }

        @Data
        @Accessors(chain = true)
        public static abstract sealed class TimeoutAction {
            @ToString(callSuper = true)
            @EqualsAndHashCode(callSuper = true)
            @Data
            @Accessors(chain = true)
            public static final class No extends TimeoutAction {
            }

            @ToString(callSuper = true)
            @EqualsAndHashCode(callSuper = true)
            @Data
            @Accessors(chain = true)
            public static final class Throw extends TimeoutAction {
            }

            @ToString(callSuper = true)
            @EqualsAndHashCode(callSuper = true)
            @Data
            @Accessors(chain = true)
            public static sealed class SigTermThenThrow extends TimeoutAction permits SigTermThenSigKillThenThrow {
                Duration timeoutAfterSigTerm = Duration.ofSeconds(5);
            }

            @ToString(callSuper = true)
            @EqualsAndHashCode(callSuper = true)
            @Data
            @Accessors(chain = true)
            public static final class SigTermThenSigKillThenThrow extends SigTermThenThrow {
                Duration timeoutAfterSigKill = Duration.ofSeconds(5);
            }
        }
    }

    @Getter
    @Setter
    @ToString
    @Accessors(chain = true)
    public static abstract class ExecException extends RuntimeException {
        ProcessConfig processConfig;
    }

    public static class ExecTimeoutException extends ExecException {
    }
}
