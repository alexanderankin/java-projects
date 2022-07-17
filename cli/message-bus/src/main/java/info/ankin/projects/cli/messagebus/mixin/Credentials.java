package info.ankin.projects.cli.messagebus.mixin;

import lombok.Data;
import lombok.ToString;
import picocli.CommandLine;

import java.util.Optional;

@Data
@CommandLine.Command(resourceBundle = "info.ankin.projects.cli.messagebus.mixin.Mixin")
public class Credentials {
    @CommandLine.Option(names = {"-h", "--host"},
            description = "Hostname of the broker")
    String host = "localhost";

    @CommandLine.Option(names = {"--virtual-host"},
            descriptionKey = "virtualHost.descriptionKey")
    String virtualHost = "/";

    //<editor-fold desc="username options">
    @CommandLine.Option(names = {"-u", "--username"},
            descriptionKey = "username.descriptionKey")
    String username;

    @CommandLine.Option(names = {"-U", "-u:env", "--username:env"},
            descriptionKey = "usernameEnvVar.descriptionKey",
            defaultValue = "MB_USER")
    String usernameEnvVar;
    //</editor-fold>

    //<editor-fold desc="password options">
    @ToString.Exclude
    @CommandLine.Option(names = {"-p", "--password"},
            arity = "0",
            interactive = true,
            descriptionKey = "password.descriptionKey")
    char[] password;

    @CommandLine.Option(names = {"-P", "-p:env", "--password:env"},
            interactive = true,
            descriptionKey = "passwordEnvVar.descriptionKey",
            defaultValue = "MB_PASS")
    String passwordEnvVar;
    //</editor-fold>

    // TODO more authentication options

    public String determineUsername() {
        return Optional.ofNullable(username)
                .or(() -> Optional.ofNullable(usernameEnvVar)
                        .map(System::getenv))
                .orElseThrow(() -> new IllegalArgumentException("Didn't supply username '-u' or query env var: " + usernameEnvVar));
    }

    public char[] determinePassword() {
        return Optional.ofNullable(password)
                .or(() -> Optional.ofNullable(passwordEnvVar)
                        .map(System::getenv)
                        .map(String::toCharArray))
                .orElseThrow(() -> new IllegalArgumentException("Didn't prompt for password '-p' or query env var: " + passwordEnvVar));
    }
}
