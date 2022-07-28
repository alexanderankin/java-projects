package info.ankin.projects.spring.app;

import lombok.Builder;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.Message;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.function.Function;

@SpringBootApplication
public class ConsumerApplication {
    public static void main(String[] args) {
        SpringApplication.run(ConsumerApplication.class, args);
    }

    @Slf4j
    @Configuration
    public static class CloudFunctionConfig {
        @Bean
        Function<Flux<Message<String>>, Mono<Void>> input() {
            return this::attachListener;
        }

        private Mono<Void> attachListener(Flux<Message<String>> f) {
            return f.doOnNext(this::processMessage).then();
        }

        private void processMessage(Message<String> m) {
            log.debug("{} {} {}", m.getPayload(), m, m.getHeaders());
            log.info(m.getPayload());
        }
    }

    @Builder(toBuilder = true, builderMethodName = "")
    @Data
    public static class Some {
        public static void main(String[] args) {
            new Some().toBuilder().build();
        }
    }
}
