package info.ankin.projects.htpasswd;

import lombok.Value;

@Value
public class HtpasswdEntry {
    public static final String HTPASSWD_LINE_SEPARATOR = ":";

    String username;
    PasswordEncryption encryption;
    char[] password;

    public String toLine() {
        return username + HTPASSWD_LINE_SEPARATOR + new String(password);
    }
}
