package info.ankin.projects.spring.app;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.Data;
import lombok.SneakyThrows;
import lombok.experimental.Accessors;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.kafka.DefaultKafkaConsumerFactoryCustomizer;
import org.springframework.boot.autoconfigure.kafka.KafkaProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpStatus;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.core.ConsumerFactory;
import org.springframework.kafka.core.DefaultKafkaConsumerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.kafka.support.SendResult;
import org.springframework.messaging.Message;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseStatus;

import java.time.Instant;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

@Slf4j
@Controller
@EnableScheduling
@Configuration
public class MyConfig {

    // @Bean
    // public ConsumerFactory<?, ?> kafkaConsumerFactory(
    //         ObjectProvider<DefaultKafkaConsumerFactoryCustomizer> customizers) {
    //     HashMap<String, Object> map = new HashMap<>();
    //     DefaultKafkaConsumerFactory<Object, Object> factory = new DefaultKafkaConsumerFactory<>(map);
    //     return factory;
    // }

    @Autowired
    KafkaTemplate<String, String> kafkaTemplate;
    @Autowired
    ObjectMapper objectMapper;

    // @KafkaListener(topics = "some.topic")
    // public void singleListener(Message<String> message) {
    //
    // }
    @KafkaListener(topics = "some.topic", groupId = "some.other.groupId")
    public void batchListener(List<ConsumerRecord<String, String>> messages) {
        System.out.println("received: " + messages);
    }

    @SneakyThrows
    @RequestMapping(method = RequestMethod.POST, path = "/trigger-endpoint")
    @ResponseStatus(HttpStatus.OK)
    @Scheduled(cron = "*/3 * * * * *")
    public void doScheduledAction() {
        System.out.println("Hi there, it is: " + Instant.now());

        try {
            Message<String> msg = MessageBuilder.withPayload(objectMapper.writeValueAsString(new TopicSchema()
                            .setMessage("message")))
                    .setHeader(KafkaHeaders.TOPIC, "some.topic")
                    .build();
            SendResult<String, String> message = kafkaTemplate.send(msg)
                    .completable()
                    .join();
            System.out.println("sent: " + message);
        } catch (Exception e) {
            log.error("whoops", e);
        }
    }

    @Accessors(chain = true)
    @Data
    public static class TopicSchema {
        Date date = new Date();
        String message;
    }
}
