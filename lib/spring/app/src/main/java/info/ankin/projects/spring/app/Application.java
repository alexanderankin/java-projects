package info.ankin.projects.spring.app;

import info.ankin.projects.spring.httpscustomizer.HttpsCustomizerProperties;
import info.ankin.projects.spring.httpscustomizer.NettyHttpsCustomizer;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.embedded.netty.NettyReactiveWebServerFactory;
import org.springframework.boot.web.server.WebServerFactoryCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@SpringBootApplication
public class Application {
    public static void main(String[] args) {
        SpringApplication.run(Application.class, args);
    }

    @Configuration
    public static class Config {
        @Bean
        public WebServerFactoryCustomizer<NettyReactiveWebServerFactory> customizer() {
            return new NettyHttpsCustomizer(new HttpsCustomizerProperties());
        }
    }

}
