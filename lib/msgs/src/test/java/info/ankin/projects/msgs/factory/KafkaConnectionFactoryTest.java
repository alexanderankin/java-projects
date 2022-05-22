package info.ankin.projects.msgs.factory;

import info.ankin.projects.msgs.ConnectionFactory;
import info.ankin.projects.msgs.model.Configuration;
import info.ankin.projects.msgs.model.Message;
import info.ankin.projects.msgs.model.Request;
import org.junit.jupiter.api.Test;
import reactor.core.Disposable;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.Duration;
import java.util.UUID;

class KafkaConnectionFactoryTest {

    @Test
    void test_consumer() {
        ConnectionFactory.Connection connection = new KafkaConnectionFactory().connection(new Configuration().setPort(29092)).block();
        assert connection != null;
        ConnectionFactory.Consumer consumer = connection.consumer(new Request()
                        .setClientId(UUID.randomUUID().toString())
                        .setSub(new Request.Sub().setGroup("KafkaConnectionFactory#test_consumer"))
                        .setTopic("test_consumer"))
                .block();

        assert consumer != null;
        Flux<Message> messages = consumer.subscribe();
        Disposable subscribe = messages.doOnNext(m -> System.out.println("got a kafka message! " + m)).subscribe();
        Mono.delay(Duration.ofSeconds(30)).doOnTerminate(subscribe::dispose).block();
    }

}
