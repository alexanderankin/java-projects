package info.ankin.how.spring.logging.json.demo;

import info.ankin.how.spring.logging.json.JsonFormatListenerConfig;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.WebApplicationType;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@SpringBootApplication
class JsonFormatSlf4jListenerDemoApplication {
    public static void main(String[] args) {
        System.setProperty("logging.level.info.ankin", "debug");
        System.setProperty("json-logging-listener.enabled", "true");
        new SpringApplicationBuilder(JsonFormatSlf4jListenerDemoApplication.class)
                .web(WebApplicationType.NONE)
                .run(args);
    }

    @Slf4j
    @Configuration
    static class Other {
        @Bean
        ApplicationRunner applicationRunner(JsonFormatListenerConfig.Props listenerProps) {
            return args -> log.info("listenerProps: {}", listenerProps);
        }
    }
}
