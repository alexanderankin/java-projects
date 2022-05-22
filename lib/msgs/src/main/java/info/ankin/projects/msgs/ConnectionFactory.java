package info.ankin.projects.msgs;

import info.ankin.projects.msgs.model.Configuration;
import info.ankin.projects.msgs.model.Message;
import info.ankin.projects.msgs.model.Request;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.core.publisher.Sinks;

public interface ConnectionFactory {
    SupportedBroker flavor();

    Mono<Connection> connection(Configuration configuration);

    interface Connection {
        Mono<Consumer> consumer(Request request);

        Mono<Producer> producer(Request request);
    }

    interface Consumer {
        Flux<Message> subscribe();
    }

    interface Producer {
        Sinks.Many<Message> publish();
    }

}
