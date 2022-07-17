package info.ankin.projects.cli.messagebus.service.publish;

import info.ankin.projects.cli.messagebus.model.Message;
import info.ankin.projects.cli.messagebus.service.Client;
import reactor.core.publisher.Mono;

public interface Publisher extends Client {
    /**
     * perform publishing
     */
    Mono<Void> publish(Message message);
}
