package info.ankin.projects.cli.messagebus.model;

import lombok.Data;
import lombok.EqualsAndHashCode;
import reactor.core.publisher.Flux;

@EqualsAndHashCode(callSuper = true)
@Data
public class PublishRequest extends Request {
    /**
     * Messages to send to the broker
     */
    Flux<Message> messages;
}
