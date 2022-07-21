package info.ankin.projects.spring.httpscustomizer;

import org.springframework.boot.web.server.ConfigurableWebServerFactory;
import org.springframework.boot.web.server.Ssl;
import org.springframework.boot.web.server.WebServerFactoryCustomizer;

import java.security.KeyStore;

public class HttpsCustomizer<T extends ConfigurableWebServerFactory> implements WebServerFactoryCustomizer<T> {
    @Override
    public void customize(T factory) {
        factory.setSsl(new Ssl());
        factory.setSslStoreProvider(KeyStoreProvider.of(getKeyStore()));
    }

    protected KeyStore getKeyStore() {
        throw new UnsupportedOperationException("not implemented yet");
    }
}
