package info.ankin.projects.system.exec;

import lombok.*;
import lombok.experimental.Accessors;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.output.NullOutputStream;
import org.apache.commons.io.output.TeeOutputStream;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.Duration;
import java.util.*;
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

        var out = pb.redirectOutput() == ProcessBuilder.Redirect.PIPE
                ? handleStream(captureOutput, processConfig.getStandardOutput(), process.getInputStream(), System.out)
                : RunningProcess.CaptureStreamHandle.NULL;
        var err = pb.redirectError() == ProcessBuilder.Redirect.PIPE
                ? handleStream(captureOutput, processConfig.getStandardError(), process.getErrorStream(), System.err)
                : RunningProcess.CaptureStreamHandle.NULL;

        return new RunningProcess()
                .setConfig(processConfig)
                .setProcess(process)
                .setStandardOutputStreamHandle(out)
                .setStandardErrorStreamHandle(err);
    }

    @SneakyThrows
    private RunningProcess.CaptureStreamHandle handleStream(boolean captureOutput,
                                                            ProcessConfig.ProcessStream.ToStream toStream,
                                                            InputStream original,
                                                            OutputStream inheritTarget) {
        var target = switch (toStream) {
            case ProcessConfig.ProcessStream.DiscardProcessStream ignored -> NullOutputStream.INSTANCE;
            case ProcessConfig.ProcessStream.InheritedProcessStream ignored -> inheritTarget;
            case ProcessConfig.ProcessStream.PipeProcessStream.FilePipeProcessStream filePipeProcessStream ->
                    Files.newOutputStream(filePipeProcessStream.getFile());
            case ProcessConfig.ProcessStream.PipeProcessStream.ProcessOutputToStream processOutputToStream ->
                    processOutputToStream.getOutputStream();
        };

        ByteArrayOutputStream capture;
        OutputStream stream;
        if (captureOutput) {
            capture = new ByteArrayOutputStream();
            stream = new TeeOutputStream(target, capture);
        } else {
            capture = null;
            stream = target;
        }
        var thread = Thread.ofVirtual().name(getClass().getSimpleName() + "-transfer-stream").start(() -> transfer(original, stream));
        return new RunningProcess.CaptureStreamHandle(capture, thread);
    }

    @SneakyThrows
    private void transfer(InputStream inputStream, OutputStream finalOutTarget) {
        inputStream.transferTo(finalOutTarget);
    }

    @Data
    @Accessors(chain = true)
    public static class CompletedProcess {
        int exitCode;
        ProcessConfig config;
        Process process;
        byte[] standardOutput;
        byte[] standardError;
        String standardOutputString;
        String standardErrorString;
    }

    @Data
    @Accessors(chain = true)
    public static class RunningProcess {
        ProcessConfig config;
        Process process;
        CaptureStreamHandle standardOutputStreamHandle;
        CaptureStreamHandle standardErrorStreamHandle;

        @SneakyThrows
        public CompletedProcess waitUntilCompletion() {
            waitForProcess(process);
            waitForThread(standardOutputStreamHandle.thread);
            waitForThread(standardErrorStreamHandle.thread);

            var standardOutputStream = standardOutputStreamHandle.stream;
            var standardErrorStream = standardErrorStreamHandle.stream;

            var completedProcess = new CompletedProcess()
                    .setExitCode(process.exitValue())
                    .setStandardOutput(standardOutputStream == null ? null : standardOutputStream.toByteArray())
                    .setStandardError(standardErrorStream == null ? null : standardErrorStream.toByteArray());

            if (completedProcess.getStandardOutput() != null && config.getStandardOutputCharset() != null)
                completedProcess.setStandardOutputString(new String(completedProcess.getStandardOutput(), config.getStandardOutputCharset()));

            if (completedProcess.getStandardError() != null && config.getStandardErrorCharset() != null)
                completedProcess.setStandardErrorString(new String(completedProcess.getStandardError(), config.getStandardErrorCharset()));

            completedProcess
                    .setConfig(config)
                    .setProcess(process);

            if (!config.allowedExitCodes.contains(completedProcess.getExitCode())) {
                throw new ExecExitCodeException().setCompletedProcess(completedProcess);
            }

            return completedProcess;
        }

        @SneakyThrows
        private void waitForProcess(Process process) {
            if (config.timeout == null) {
                process.waitFor();
            } else {
                if (!process.waitFor(TimeUnit.NANOSECONDS.convert(config.timeout), TimeUnit.NANOSECONDS)) {
                    switch (config.timeoutAction) {
                        case ProcessConfig.TimeoutAction.No ignored -> {
                        }
                        case ProcessConfig.TimeoutAction.Throw ignored ->
                                throw new ExecTimeoutException().setRunningProcess(this);
                        case ProcessConfig.TimeoutAction.SigTermThenKillThenThrow termKillThrow -> {
                            if (!process.waitFor(TimeUnit.NANOSECONDS.convert(termKillThrow.timeoutAfterSigTerm), TimeUnit.NANOSECONDS)) {
                                process.destroyForcibly();
                                if (!process.waitFor(TimeUnit.NANOSECONDS.convert(termKillThrow.timeoutAfterSigKill), TimeUnit.NANOSECONDS)) {
                                    throw new ExecTimeoutException().setRunningProcess(this);
                                }
                            }
                        }
                        case ProcessConfig.TimeoutAction.SigTermThenThrow termThenThrow -> {
                            process.destroy();
                            if (!process.waitFor(TimeUnit.NANOSECONDS.convert(termThenThrow.timeoutAfterSigTerm), TimeUnit.NANOSECONDS)) {
                                throw new ExecTimeoutException().setRunningProcess(this);
                            }
                        }
                    }
                }
            }
        }

        @SneakyThrows
        private void waitForThread(Thread thread) {
            if (thread == null)
                return;
            if (config.timeout == null) {
                thread.join();
            } else {
                thread.join(config.timeout.toMillis());
                if (thread.isAlive() && thread.getState() != Thread.State.TERMINATED) {
                    switch (config.timeoutAction) {
                        case ProcessConfig.TimeoutAction.No ignored -> {
                        }
                        case ProcessConfig.TimeoutAction.Throw ignored ->
                                throw new ExecTimeoutException().setRunningProcess(this);
                        case ProcessConfig.TimeoutAction.SigTermThenKillThenThrow termKillThrow -> {
                            thread.interrupt();
                            thread.join(termKillThrow.timeoutAfterSigTerm.toMillis());
                            if (thread.isAlive() && thread.getState() != Thread.State.TERMINATED) {
                                thread.interrupt();
                                thread.join(termKillThrow.timeoutAfterSigKill.toMillis());
                                if (thread.isAlive() && thread.getState() != Thread.State.TERMINATED) {
                                    throw new ExecTimeoutException().setRunningProcess(this);
                                }
                            }
                        }
                        case ProcessConfig.TimeoutAction.SigTermThenThrow termThenThrow -> {
                            thread.interrupt();
                            if (!thread.join(termThenThrow.timeoutAfterSigTerm)) {
                                throw new ExecTimeoutException().setRunningProcess(this);
                            }
                        }
                    }
                }
            }
        }

        record CaptureStreamHandle(ByteArrayOutputStream stream, Thread thread) {
            static final CaptureStreamHandle NULL = new CaptureStreamHandle(null, null);
        }
    }

    @Data
    @Accessors(chain = true)
    public static class ProcessConfig {
        public static final Set<Integer> DEFAULT_ALLOWED_EXIT_CODES = Set.of(0);
        Path workingDirectory;
        String command;
        List<String> arguments;
        Map<String, String> environment;
        boolean captureOutput;
        ProcessStream.FromStream standardInput = ProcessStream.InheritedProcessStream.INSTANCE;
        ProcessStream.ToStream standardOutput = ProcessStream.InheritedProcessStream.INSTANCE;
        ProcessStream.ToStream standardError = ProcessStream.InheritedProcessStream.INSTANCE;
        Duration timeout;
        TimeoutAction timeoutAction = new TimeoutAction.SigTermThenKillThenThrow();
        Charset standardOutputCharset;
        Charset standardErrorCharset;
        Set<Integer> allowedExitCodes = DEFAULT_ALLOWED_EXIT_CODES;

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

        public ProcessConfig streamStandardInput(InputStream stream) {
            standardInput = new ProcessStream.PipeProcessStream.PipeProcessStream.ProcessInputFromStream().setInputStream(stream);
            return this;
        }

        public ProcessConfig streamStandardOutput(OutputStream stream) {
            standardOutput = new ProcessStream.PipeProcessStream.PipeProcessStream.ProcessOutputToStream().setOutputStream(stream);
            return this;
        }

        public ProcessConfig streamStandardError(OutputStream stream) {
            standardError = new ProcessStream.PipeProcessStream.PipeProcessStream.ProcessOutputToStream().setOutputStream(stream);
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
                    InputStream inputStream;
                }

                @ToString(callSuper = true)
                @EqualsAndHashCode(callSuper = true)
                @Data
                @Accessors(chain = true)
                public static final class ProcessOutputToStream extends PipeProcessStream implements ToStream {
                    OutputStream outputStream;
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
                public static final No INSTANCE = new No();
            }

            @ToString(callSuper = true)
            @EqualsAndHashCode(callSuper = true)
            @Data
            @Accessors(chain = true)
            public static final class Throw extends TimeoutAction {
                public static final Throw INSTANCE = new Throw();
            }

            @ToString(callSuper = true)
            @EqualsAndHashCode(callSuper = true)
            @Data
            @Accessors(chain = true)
            public static sealed class SigTermThenThrow extends TimeoutAction permits SigTermThenKillThenThrow {
                public static final SigTermThenThrow DEFAULT = new SigTermThenThrow();
                Duration timeoutAfterSigTerm = Duration.ofSeconds(5);
            }

            @ToString(callSuper = true)
            @EqualsAndHashCode(callSuper = true)
            @Data
            @Accessors(chain = true)
            public static final class SigTermThenKillThenThrow extends SigTermThenThrow {
                public static final SigTermThenKillThenThrow DEFAULT = new SigTermThenKillThenThrow();
                Duration timeoutAfterSigKill = Duration.ofSeconds(5);
            }
        }
    }

    public static abstract class ExecException extends RuntimeException {
    }

    @Getter
    @Setter
    @ToString(callSuper = true)
    @Accessors(chain = true)
    public static class ExecTimeoutException extends ExecException {
        RunningProcess runningProcess;
    }

    @Getter
    @Setter
    @ToString(callSuper = true)
    @Accessors(chain = true)
    public static class ExecExitCodeException extends ExecException {
        CompletedProcess completedProcess;

        public int getExitCode() {
            return completedProcess.getExitCode();
        }
    }
}
