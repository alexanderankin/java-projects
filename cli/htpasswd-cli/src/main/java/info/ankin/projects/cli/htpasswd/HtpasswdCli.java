package info.ankin.projects.cli.htpasswd;

import info.ankin.projects.htpasswd.Htpasswd;
import info.ankin.projects.htpasswd.HtpasswdEntry;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import picocli.CommandLine;

import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.security.SecureRandom;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.stream.Collectors;

@Slf4j
@CommandLine.Command(
        name = "htpasswd",
        description = "utility for managing htpasswd files",
        mixinStandardHelpOptions = true
)
public class HtpasswdCli implements Callable<Integer> {
    private static final int MIN_PARAMETERS = 2;

    @CommandLine.Mixin
    Options options;

    @CommandLine.Parameters(arity = "2..")
    List<String> parameters;

    public static void main(String[] args) {
        System.exit(new CommandLine(new HtpasswdCli()).execute(args));
    }

    @Override
    public Integer call() throws Exception {
        Options.OperationMode operationMode = options.operationMode();
        int maxParameters = operationMode == Options.OperationMode.display ? 2 : 3;
        if (parameters.size() < MIN_PARAMETERS || parameters.size() > maxParameters)
            throw new IllegalArgumentException("too many parameters (" + parameters.size() + ", more than" + maxParameters + ")");

        Params params = new Params(parameters);

        Htpasswd htpasswd = new Htpasswd(new SecureRandom(), options.htpasswdProperties());

        switch (operationMode) {
            case add: {
                if (params.getPasswordFile() == null)
                    throw new IllegalArgumentException("need passwordFile to add");

                List<HtpasswdEntry> entries = Files.readAllLines(Path.of(params.getPasswordFile()))
                        .stream().map(htpasswd::parse)
                        .collect(Collectors.toList());

                entries.add(htpasswd.create(params.getUsername(),
                        options.passwordEncryption(),
                        params.getPassword().toCharArray()));

                Files.write(Path.of(params.getPasswordFile()),
                        entries.stream()
                                .map(HtpasswdEntry::toLine)
                                .collect(Collectors.joining(System.lineSeparator()))
                                .getBytes(StandardCharsets.UTF_8));
                break;
            }
            case create: {
                String s = htpasswd.create(params.getUsername(),
                        options.passwordEncryption(),
                        params.getPassword().toCharArray()).toLine();

                Files.write(Path.of(params.getPasswordFile()), s.getBytes(StandardCharsets.UTF_8));
                break;
            }
            case delete: {
                List<HtpasswdEntry> entries = Files.readAllLines(Path.of(params.getPasswordFile()))
                        .stream().map(htpasswd::parse)
                        .collect(Collectors.toList());

                entries.removeIf(e -> e.getUsername().equals(params.getUsername()));

                Files.write(Path.of(params.getPasswordFile()),
                        entries.stream()
                                .map(HtpasswdEntry::toLine)
                                .collect(Collectors.joining(System.lineSeparator()))
                                .getBytes(StandardCharsets.UTF_8));
                break;
            }
            case display: {
                String s = htpasswd.create(params.getUsername(),
                        options.passwordEncryption(),
                        params.getPassword().toCharArray()).toLine();

                System.out.print(s);
                break;
            }
            case verify: {
                throw new UnsupportedOperationException("not yet implemented");
            }
        }

        log.info("{}", parameters);
        return 0;
    }

    @AllArgsConstructor
    @Data
    static class Params {
        private final String WRONG_PARAMS = "wrong length of parameters (not 2 or 3): ";

        String passwordFile;
        String username;
        String password;

        public Params(List<String> params) {
            if (params.size() == 3) {
                passwordFile = params.get(0);
                username = params.get(1);
                password = params.get(2);
            } else if (params.size() == 2) {
                username = params.get(0);
                password = params.get(1);
            } else {
                throw new IllegalStateException(WRONG_PARAMS + params.size());
            }
        }
    }
}
