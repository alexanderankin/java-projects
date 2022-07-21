package info.ankin.projects.spring.httpscustomizer;

import org.springframework.boot.web.server.SslStoreProvider;

import java.security.KeyStore;

public class KeyStoreProvider implements SslStoreProvider {
    private final KeyStore keyStore;

    public KeyStoreProvider(KeyStore keyStore) {
        this.keyStore = keyStore;
    }

    public static SslStoreProvider of(KeyStore keyStore) {
        return new KeyStoreProvider(keyStore);
    }

    @Override
    public KeyStore getKeyStore() throws Exception {
        return null;
    }

    @Override
    public KeyStore getTrustStore() throws Exception {
        return null;
    }
}
