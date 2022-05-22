package info.ankin.projects.msgs.model;

import lombok.Data;
import lombok.experimental.Accessors;

/**
 * Information necessary to subscribe or publish to a broker
 */
@Accessors(chain = true)
@Data
public class Request {
    String topic;
    String clientId;
    long sendingTimeout;
    Pub pub;
    Sub sub;

    public Pub getPub() {
        if (pub == null) pub = new Pub();
        return pub;
    }

    public Sub getSub() {
        if (sub == null) sub = new Sub();
        return sub;
    }

    @Accessors(chain = true)
    @Data
    public static class Sub {
        String group;
    }

    @Accessors(chain = true)
    @Data
    public static class Pub {
        int replication;
    }

}
