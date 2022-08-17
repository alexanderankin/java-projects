package info.ankin.projects.cli.htpasswd;

import info.ankin.projects.cli.htpasswd.exception.MultipleEncryptionException;
import info.ankin.projects.cli.htpasswd.exception.MultipleModesException;
import info.ankin.projects.htpasswd.HtpasswdProperties;
import info.ankin.projects.htpasswd.SupportedEncryption;
import lombok.Data;
import lombok.SneakyThrows;
import picocli.CommandLine;

import java.util.EnumMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

@Data
@CommandLine.Command
public class Options {

    @CommandLine.Option(names = "-c",
            description = "Create a new file.")
    boolean createMode;

    @CommandLine.Option(names = "-n",
            description = "Don't update file; display results on stdout.")
    boolean displayMode;

    @CommandLine.Option(names = "-b",
            description = "Use the password from the command line rather than prompting for it.")
    boolean noPrompt;

    @CommandLine.Option(names = "-i",
            description = "Read password from stdin without verification (for script usage).")
    boolean stdinPassword;

    @CommandLine.Option(names = "-m",
            description = "Force MD5 encryption of the password (default).")
    boolean useMd5;

    @CommandLine.Option(names = "-B",
            description = "Force bcrypt encryption of the password (very secure).")
    boolean useBcrypt;

    @CommandLine.Option(names = "-C",
            description = "Set the computing time used for the bcrypt algorithm" +
                    "(higher is more secure but slower, default: 5, valid: 4 to 17).")
    Integer bcryptCost = 12;

    @CommandLine.Option(names = "-d",
            description = "Force CRYPT encryption of the password (8 chars max, insecure).")
    boolean useCrypt;

    @CommandLine.Option(names = "-s",
            description = "Force SHA encryption of the password (insecure).")
    boolean useSha;

    @CommandLine.Option(names = "-p",
            description = "Do not encrypt the password (plaintext, insecure).")
    boolean usePlain;

    @CommandLine.Option(names = "-D",
            description = "Delete the specified user.")
    boolean deleteMode;

    @CommandLine.Option(names = "-v",
            description = "Verify password for the specified user.")
    boolean verifyMode;

    SupportedEncryption passwordEncryption() {
        EnumMap<SupportedEncryption, Boolean> map = new EnumMap<>(SupportedEncryption.class);
        map.put(SupportedEncryption.bcrypt, isUseBcrypt());
        map.put(SupportedEncryption.crypt, isUseCrypt());
        map.put(SupportedEncryption.md5, isUseMd5());
        map.put(SupportedEncryption.plain, isUsePlain());
        map.put(SupportedEncryption.sha, isUseSha());

        return maxOneOrThrow(map, SupportedEncryption.bcrypt, MultipleEncryptionException::new);
    }

    OperationMode operationMode() {
        EnumMap<OperationMode, Boolean> map = new EnumMap<>(OperationMode.class);
        map.put(OperationMode.create, isCreateMode());
        map.put(OperationMode.delete, isDeleteMode());
        map.put(OperationMode.display, isDisplayMode());
        map.put(OperationMode.verify, isVerifyMode());
        return maxOneOrThrow(map, OperationMode.add, MultipleModesException::new);
    }

    @SneakyThrows
    <T extends Enum<?>> T maxOneOrThrow(Map<T, Boolean> map, T defaultIfOne, Function<String, Exception> exceptionCreator) {
        List<Map.Entry<T, Boolean>> list = map.entrySet().stream().filter(Map.Entry::getValue).collect(Collectors.toList());
        if (list.size() == 0) return defaultIfOne;
        if (list.size() == 1) return list.get(0).getKey();

        String multiple = list.stream().map(Map.Entry::getKey).map(Enum::name).collect(Collectors.joining(", "));
        throw exceptionCreator.apply("Can't select multiple: " + multiple);
    }

    HtpasswdProperties htpasswdProperties() {
        return new HtpasswdProperties()
                .setBcryptCost(getBcryptCost())
                ;
    }

    enum OperationMode {
        add,
        create,
        delete,
        display,
        verify,
    }

}
