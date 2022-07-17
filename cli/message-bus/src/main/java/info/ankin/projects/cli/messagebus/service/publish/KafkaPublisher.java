package info.ankin.projects.cli.messagebus.service.publish;

import info.ankin.projects.cli.messagebus.model.BrokerInformation;
import info.ankin.projects.cli.messagebus.model.BrokerType;
import info.ankin.projects.cli.messagebus.model.Message;
import org.apache.kafka.clients.producer.KafkaProducer;
import reactor.core.publisher.Mono;

import java.util.Properties;

import static org.apache.kafka.clients.producer.ProducerConfig.*;

public class KafkaPublisher implements Publisher {
    private KafkaProducer<String, String> kafkaProducer;

    @Override
    public BrokerType type() {
        return BrokerType.kafka;
    }

    @Override
    public Mono<Void> init(BrokerInformation brokerInformation) {
        kafkaProducer = new KafkaProducer<>(toProducerProperties(brokerInformation));
    }

    private Properties toProducerProperties(BrokerInformation brokerInformation) {
        Properties properties = new Properties();
        properties.put(KEY_SERIALIZER_CLASS_CONFIG, "org.apache.kafka.common.serialization.StringSerializer");
        properties.put(VALUE_SERIALIZER_CLASS_CONFIG, "org.apache.kafka.common.serialization.StringSerializer");
        properties.put(BOOTSTRAP_SERVERS_CONFIG, brokerInformation.getHost());
        properties.put(RETRIES_CONFIG, brokerInformation.getRetries());
        return properties;
    }

    @Override
    public Mono<Void> publish(Message message) {
        throw new UnsupportedOperationException("not yet implemented");
    }
}
