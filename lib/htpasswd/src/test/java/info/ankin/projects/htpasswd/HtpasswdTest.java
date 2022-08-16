package info.ankin.projects.htpasswd;

import org.junit.jupiter.api.Test;

import java.security.SecureRandom;
import java.util.List;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * {@code htpasswd} flags:
 * <pre>
 * -b  Use the password from the command line rather than prompting for it.
 * -m  Force MD5 encryption of the password (default).
 * -B  Force bcrypt encryption of the password (very secure).
 * -d  Force CRYPT encryption of the password (8 chars max, insecure).
 * -s  Force SHA encryption of the password (insecure).
 * -p  Do not encrypt the password (plaintext, insecure).
 * </pre>
 */
class HtpasswdTest {
    static final String sampleData = "abc_B:$2y$05$E.CCdKfoLq0wTa0xtZWqKeiCpJ8yl5Qdh8OEDz5alA1scbJ9yAke6\n" +
            "abc_d:JX/JbiqtIyQeQ\n" +
            "abc_m:$apr1$oGQDCI3x$KOpxsCOmTuabfahpbuZL3.\n" +
            "abc_p:def\n" +
            "abc_s:{SHA}WJwiM1o4HxItEpIl9cC6MFbtWBE=";

    Htpasswd htpasswd = new Htpasswd(new SecureRandom(), new HtpasswdProperties().setBcryptCost(4));

    @Test
    void test_recognizesAllTypes() {
        List<String> split = List.of(sampleData.split("\n"));

        List<HtpasswdEntry> lines = split.stream().map(htpasswd::parse).collect(Collectors.toList());

        for (HtpasswdEntry line : lines) {
            assertTrue(htpasswd.check(line, "def".toCharArray()), "matches for " + line.getEncryption());
        }
    }

    @Test
    void test_changePasswordToBcrypt() {
        for (var e : SupportedEncryption.values()) {
            // create entry and verify
            var line = htpasswd.create("test_changePasswordToBcrypt", e, ("password" + e).toCharArray());
            assertTrue(htpasswd.check(line, ("password" + e).toCharArray()), "new line " + line + " from " + e);

            // verify not new password
            char[] newPassword = "password1".toCharArray();
            assertFalse(htpasswd.check(line, newPassword));
            // change password and verify
            line = htpasswd.changePassword(line, newPassword);
            assertTrue(htpasswd.check(line, newPassword));
        }
    }

    @Test
    void test_changePasswordToAny() {
        for (var e : SupportedEncryption.values()) {
            // create entry and verify
            var line = htpasswd.create("test_changePasswordToBcrypt", e, ("password" + e).toCharArray());
            assertTrue(htpasswd.check(line, ("password" + e).toCharArray()), "new line " + line + " from " + e);

            // verify not new password
            char[] newPassword = "password1".toCharArray();
            assertFalse(htpasswd.check(line, newPassword));
            // change password and verify
            for (var to : SupportedEncryption.values()) {
                line = htpasswd.changePassword(line, newPassword, to);
                assertTrue(htpasswd.check(line, newPassword));

            }
        }
    }
}
