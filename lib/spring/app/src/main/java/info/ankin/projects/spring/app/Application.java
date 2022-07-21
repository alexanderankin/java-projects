package info.ankin.projects.spring.app;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.embedded.netty.NettyReactiveWebServerFactory;
import org.springframework.boot.web.server.Ssl;
import org.springframework.boot.web.server.SslStoreProvider;
import org.springframework.boot.web.server.WebServerFactoryCustomizer;
import org.springframework.context.annotation.Configuration;

import java.security.KeyStore;

@SpringBootApplication
public class Application {
    public static void main(String[] args) {
        SpringApplication.run(Application.class, args);
    }

    @Configuration
    public static class Config {
        public WebServerFactoryCustomizer<NettyReactiveWebServerFactory> customizer() {
            return new WebServerFactoryCustomizer<>() {
                @Override
                public void customize(NettyReactiveWebServerFactory factory) {
                    factory.setSsl(new Ssl());
                    factory.setSslStoreProvider(new SingleCertSslStoreProvider(getKeyStore()));
                }

                private KeyStore getKeyStore() {
                    return new OpenSslRunner().runOpenSsl();
                }
            };
        }
    }

    public static class SingleCertSslStoreProvider implements SslStoreProvider {
        private final KeyStore keyStore;

        public SingleCertSslStoreProvider(KeyStore keyStore) {
            this.keyStore = keyStore;
        }

        @Override
        public KeyStore getKeyStore() {
            return keyStore;
        }

        @Override
        public KeyStore getTrustStore() {
            return null;
        }
    }
}
