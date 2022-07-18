package info.ankin.projects.cli.messagebus.service.publish;

import info.ankin.projects.cli.messagebus.model.BrokerInformation;
import info.ankin.projects.cli.messagebus.model.BrokerType;
import info.ankin.projects.cli.messagebus.model.CfWrapper;
import info.ankin.projects.cli.messagebus.model.Message;
import jakarta.inject.Singleton;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.clients.producer.RecordMetadata;
import reactor.core.publisher.Mono;

import java.util.Properties;

import static org.apache.kafka.clients.producer.ProducerConfig.*;

@Singleton
public class KafkaPublisher implements Publisher {
    private BrokerInformation brokerInformation;
    private KafkaProducer<String, String> kafkaProducer;

    @Override
    public BrokerType type() {
        return BrokerType.kafka;
    }

    @Override
    public Mono<Void> init(BrokerInformation brokerInformation) {
        this.brokerInformation = brokerInformation;
        kafkaProducer = new KafkaProducer<>(toProducerProperties(brokerInformation));
        return initializeEagerly();
    }

    Properties toProducerProperties(BrokerInformation brokerInformation) {
        Properties properties = new Properties();
        properties.put(KEY_SERIALIZER_CLASS_CONFIG, "org.apache.kafka.common.serialization.StringSerializer");
        properties.put(VALUE_SERIALIZER_CLASS_CONFIG, "org.apache.kafka.common.serialization.StringSerializer");
        properties.put(BOOTSTRAP_SERVERS_CONFIG, brokerInformation.getHost());
        properties.put(RETRIES_CONFIG, brokerInformation.getRetries());
        // properties.put(CLIENT_ID_CONFIG, brokerInformation.get());
        return properties;
    }

    private Mono<Void> initializeEagerly() {
        return Mono.fromCallable(() -> kafkaProducer.partitionsFor(brokerInformation.getTopic())).then();
    }

    @Override
    public Mono<Void> publish(Message message) {
        CfWrapper<RecordMetadata> wrapper = new CfWrapper<>();
        kafkaProducer.send(toRecord(message), wrapper.getCallback()::accept);
        return Mono.fromFuture(wrapper.getCf()).then();
    }

    private ProducerRecord<String, String> toRecord(Message message) {
        return new ProducerRecord<>(message.getTopic(), message.getKey(), message.getBody().getValue());
    }
}
