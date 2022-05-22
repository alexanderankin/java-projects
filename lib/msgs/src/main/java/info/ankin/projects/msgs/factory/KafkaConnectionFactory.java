package info.ankin.projects.msgs.factory;

import info.ankin.projects.msgs.SupportedBroker;
import info.ankin.projects.msgs.model.Configuration;
import info.ankin.projects.msgs.model.Message;
import info.ankin.projects.msgs.model.Request;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.core.publisher.Sinks;

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
            return Mono.just(new KafkaConsumer(request));
        }

        @Override
        public Mono<Producer> producer(Request request) {
            return Mono.just(new KafkaProducer(request));
        }
    }

    public static class KafkaConsumer extends BaseConsumer {
        private final Request request;

        public KafkaConsumer(Request request) {
            this.request = request;
        }

        @Override
        public Flux<Message> subscribe() {
            return null;
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
