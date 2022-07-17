package info.ankin.projects.cli.messagebus.model;

import lombok.Data;
import lombok.experimental.Tolerate;

import java.util.Arrays;
import java.util.Map;

@Data
public class Message {
    /**
     * exchange or topic which the message came from
     */
    String topic;

    /**
     * When the message is time-stamped.
     */
    Long timestamp;

    /**
     * What queue or partition the message came from
     */
    String partition;

    /**
     * Message metadata
     */
    Map<String, Value> headers;

    /**
     * Message contents
     */
    Value body;

    @lombok.Value
    public static class Value {
        String value;
        byte[] bytes;

        @Tolerate
        public Value(String value) {
            this.value = value;
            this.bytes = null;
        }

        @Tolerate
        public Value(byte[] bytes) {
            this.value = null;
            this.bytes = bytes;
        }

        @Override
        public String toString() {
            if (value != null) return value;
            return Arrays.toString(bytes);
        }
    }

}
