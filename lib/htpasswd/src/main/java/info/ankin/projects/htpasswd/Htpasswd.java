package info.ankin.projects.htpasswd;

import info.ankin.projects.htpasswd.exception.UnknownEncryptionException;
import org.apache.commons.codec.binary.Base64;
import org.apache.commons.codec.digest.Crypt;
import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.codec.digest.Md5Crypt;
import org.bouncycastle.crypto.generators.OpenBSDBCrypt;

import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.nio.charset.StandardCharsets;
import java.security.SecureRandom;
import java.util.Arrays;

public class Htpasswd {
    private final SecureRandom secureRandom;
    private final HtpasswdProperties htpasswdProperties;

    public Htpasswd(SecureRandom secureRandom,
                    HtpasswdProperties htpasswdProperties) {
        this.secureRandom = secureRandom;
        this.htpasswdProperties = htpasswdProperties;
    }

    public HtpasswdEntry parse(String line) {
        if (line.indexOf(':') < 0) return null;
        String[] parts = line.split(":");
        return new HtpasswdEntry(parts[0], recognize(parts[1]), parts[1].toCharArray());
    }

    public PasswordEncryption recognize(String line) {
        if (line.startsWith("$apr1")) return PasswordEncryption.md5;
        if (line.startsWith("{SHA}")) return PasswordEncryption.sha;
        if (line.startsWith("$")) return PasswordEncryption.bcrypt;
        return PasswordEncryption.crypt_or_plain;
    }

    public HtpasswdEntry create(String username,
                                SupportedEncryption encryption,
                                char[] password) {
        return new HtpasswdEntry(username,
                toPasswordEncryption(encryption),
                encrypt(encryption, password));
    }

    private PasswordEncryption toPasswordEncryption(SupportedEncryption encryption) {
        switch (encryption) {
            case bcrypt:
                return PasswordEncryption.bcrypt;
            case crypt:
            case plain:
                return PasswordEncryption.crypt_or_plain;
            case md5:
                return PasswordEncryption.md5;
            case sha:
                return PasswordEncryption.sha;
        }
        return null;
    }

    public boolean check(HtpasswdEntry entry, char[] input) {
        return verify(entry.getPassword(), entry.getEncryption(), input);
    }

    public HtpasswdEntry changePassword(HtpasswdEntry entry, char[] password) {
        return changePassword(entry, password, SupportedEncryption.bcrypt);
    }

    public HtpasswdEntry changePassword(HtpasswdEntry entry, char[] password, SupportedEncryption encryption) {
        return create(entry.getUsername(), encryption, password);
    }

    public char[] encrypt(SupportedEncryption encryption, char[] password) {
        if (encryption == null) return null;
        byte[] bytes = toBytes(password);
        switch (encryption) {
            case crypt:
                return Crypt.crypt(bytes).toCharArray();
            case bcrypt:
                return OpenBSDBCrypt.generate(todoVerifyMe(password), randomBcryptBytes(), htpasswdProperties.getBcryptCost()).toCharArray();
            case md5:
                return Md5Crypt.apr1Crypt(bytes).toCharArray();
            case sha:
                return ("{SHA}" + Base64.encodeBase64String(DigestUtils.sha1(bytes))).toCharArray();
            case plain:
                return password;
            default:
                throw new UnknownEncryptionException();
        }
    }

    // is this really how we should be doing this, no probably not
    private byte[] todoVerifyMe(char[] password) {
        return new String(password).getBytes(StandardCharsets.UTF_8);
    }

    public boolean verify(char[] password, PasswordEncryption encryption, char[] input) {
        if (encryption == null) return false;
        String hash = new String(password);

        switch (encryption) {
            case crypt_or_plain:
                return Crypt.crypt(toBytes(input), hash).equals(hash) || Arrays.equals(password, input);
            case bcrypt:
                return OpenBSDBCrypt.checkPassword(hash, input);
            case md5:
                return Md5Crypt.apr1Crypt(toBytes(input), hash).equals(hash);
            case sha:
                return Arrays.equals(password, encrypt(SupportedEncryption.sha, input));
            default:
                throw new UnknownEncryptionException();
        }
    }

    private byte[] toBytes(char[] password) {
        CharBuffer charBuffer = CharBuffer.wrap(password);
        ByteBuffer byteBuffer = StandardCharsets.UTF_8.encode(charBuffer);
        return byteBuffer.array();
    }

    private byte[] randomBcryptBytes() {
        byte[] randomBcryptBytes = new byte[16];
        secureRandom.nextBytes(randomBcryptBytes);
        return randomBcryptBytes;
    }
}
