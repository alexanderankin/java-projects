package info.ankin.projects.cli.messagebus.model;

import lombok.Data;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper = true)
@Data
public class SubscribeRequest extends Request {
    /**
     * queue - if provided and type is rabbit - will ignore {@link #getTopic()} return value.
     */
    String queue;

    /**
     * number of messages to receive before cancelling subscription.
     * If null, will listen forever.
     */
    Integer limit;
}
