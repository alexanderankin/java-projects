package info.ankin.projects.cli.messagebus.model;

import lombok.Data;

@Data
public abstract class Request {
    /**
     * This is the equivalent of a:
     * <ul>
     *     <li><code>client.id</code> in kafka</li>
     *     <li><code>???</code> in rabbitmq</li>
     *     <li><code>identifier</code> in mqtt</li>
     * </ul>
     *
     * @see <a href="https://kafka.apache.org/documentation/#producerconfigs_client.id">client.id</a>
     * @see <a href="https://docs.oasis-open.org/mqtt/mqtt/v5.0/os/mqtt-v5.0-os.html#_Toc3901059">identifier</a>
     */
    String clientId;

    /**
     * This is the equivalent of a:
     * <ul>
     *     <li><code>consumer group</code> in kafka</li>
     *     <li><code>queue</code> in rabbitmq</li>
     *     <li><code>share name</code> in mqtt</li>
     * </ul>
     *
     * @see <a href="https://kafka.apache.org/documentation/#consumerconfigs_group.id">group.id consumer config (Kafka)</a>
     * @see <a href="https://www.rabbitmq.com/queues.html">queues in RabbitMQ</a>
     * @see <a href="https://www.hivemq.com/docs/hivemq/4.8/user-guide/shared-subscriptions.html">shared subscriptions</a>
     */
    String consumerGroup;

    String topic;
}
