package info.ankin.projects.msgs.factory;

import info.ankin.projects.msgs.SupportedBroker;
import info.ankin.projects.msgs.model.Configuration;
import info.ankin.projects.msgs.model.Message;
import info.ankin.projects.msgs.model.Request;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.common.header.Header;
import org.apache.kafka.common.serialization.StringDeserializer;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.core.publisher.Sinks;
import reactor.kafka.receiver.KafkaReceiver;
import reactor.kafka.receiver.ReceiverOptions;
import reactor.kafka.receiver.ReceiverRecord;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

public class KafkaConnectionFactory extends BaseConnectionFactory {

    @Override
    public SupportedBroker flavor() {
        return SupportedBroker.Kafka;
    }

    @Override
    public Mono<Connection> connection(Configuration configuration) {
        return Mono.just(new KafkaConnection(configuration));
    }

    public static class KafkaConnection extends BaseConnection {
        private final Configuration configuration;

        public KafkaConnection(Configuration configuration) {
            this.configuration = configuration;
        }

        @Override
        public Mono<Consumer> consumer(Request request) {
            return Mono.just(new KafkaConsumer(request, configuration));
        }

        @Override
        public Mono<Producer> producer(Request request) {
            return Mono.just(new KafkaProducer(request));
        }
    }

    public static class KafkaConsumer extends BaseConsumer {
        private final Request request;
        private final Configuration configuration;

        public KafkaConsumer(Request request, Configuration configuration) {
            this.request = request;
            this.configuration = configuration;
        }

        @Override
        public Flux<Message> subscribe() {
            return KafkaReceiver.create(ReceiverOptions.create(toConfigProperties(request)).subscription(Collections.singleton(request.getTopic()))).receive().map(this::toMessage);
        }

        private Map<String, Object> toConfigProperties(Request request) {
            Map<String, Object> props = new HashMap<>();
            props.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, configuration.getHost() + ":" + configuration.getPort());
            props.put(ConsumerConfig.CLIENT_ID_CONFIG, request.getClientId());
            props.put(ConsumerConfig.GROUP_ID_CONFIG, request.getSub().getGroup());
            props.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class);
            props.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class);
            props.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "earliest");
            return props;
        }

        private Message toMessage(ReceiverRecord<Object, Object> o) {
            return new Message()
                    .setContents(String.valueOf(o.value()))
                    .setTopic(o.topic())
                    .setHeader(Message.Headers.Partition, o.partition())
                    .setHeader(Message.Headers.Offset, o.offset())
                    .setHeader(Message.Headers.Timestamp, o.timestamp())
                    .setHeader(Message.Headers.TimestampType, o.timestampType())
                    .setHeader(Message.Headers.SerializedKeySize, o.serializedKeySize())
                    .setHeader(Message.Headers.SerializedValueSize, o.serializedValueSize())
                    .setHeader(Message.Headers.Key, o.key())
                    .addHeaders(toList(o.headers()).stream().map(this::toHeader).collect(Collectors.toList()));
        }

        private Message.Header toHeader(Header header) {
            return new Message.Header().setName(header.key()).setValue(header.value());
        }
    }

    public static class KafkaProducer extends BaseProducer {
        private final Request request;

        public KafkaProducer(Request request) {
            this.request = request;
        }

        @Override
        public Sinks.Many<Message> publish() {
            return null;
        }
    }
}
