package info.ankin.projects.cli.messagebus.service.publish;

import info.ankin.projects.cli.messagebus.model.Message;
import reactor.core.publisher.Mono;

public class KafkaPublisher implements Publisher {
    @Override
    public Mono<Void> publish(Message message) {
        throw new UnsupportedOperationException("not yet implemented");
    }
}
