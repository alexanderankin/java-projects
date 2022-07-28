package info.ankin.projects.spring.app;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.Message;
import org.springframework.messaging.support.MessageBuilder;
import reactor.core.publisher.Flux;

import java.time.Duration;
import java.util.function.Supplier;
import java.util.stream.IntStream;

@SpringBootApplication
public class ProducerApplication {
    public static void main(String[] args) {
        SpringApplication.run(ProducerApplication.class, args);
    }

    @Slf4j
    @Configuration
    public static class CloudFunctionConfig {
        @Bean
        Supplier<Flux<Message<String>>> output() {
            return this::getFlux;
        }

        private Flux<Message<String>> getFlux() {
            return Flux.fromStream(IntStream.range(0, 100000).boxed())
                    .delayElements(Duration.ofSeconds(1))
                    .map(Object::toString)
                    .map(this::toMessage);
        }

        private Message<String> toMessage(String s) {
            log.info("sending message number: {}", s);
            return MessageBuilder.withPayload(s)
                    .setHeader("someHeader", "some value: " + s)
                    .build();
        }
    }
}
