package info.ankin.projects.htpasswd;

/**
 * Encryption of a password entry
 */
public enum PasswordEncryption {
    bcrypt,
    crypt_or_plain,
    md5,
    sha,
}
