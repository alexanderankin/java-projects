package info.ankin.projects.htpasswd;

/**
 * Encryption to create a password with
 */
public enum SupportedEncryption {
    bcrypt,
    crypt,
    md5,
    plain,
    sha,
}
