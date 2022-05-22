package info.ankin.projects.msgs.model;

import lombok.Data;
import lombok.experimental.Accessors;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.function.Predicate;

@Accessors(chain = true)
@Data
public class Message {
    private String contents;
    private List<Header> headers;

    public List<Header> getHeaders() {
        if (headers == null) headers = new ArrayList<>();
        return headers;
    }

    public List<Header> headers() {
        if (headers == null) return Collections.emptyList();
        return headers;
    }

    public Object getHeader(Headers header) {
        return headers().stream().filter(new HeaderMatcher(header.name())).map(Header::getValue).findAny().orElse(null);
    }

    public Message setHeader(Headers name, Object value) {
        for (Header header : getHeaders()) {
            if (header.getName().equals(name.name())) {
                header.setValue(value);
                return this;
            }
        }

        getHeaders().add(new Header().setName(name.name()).setValue(value));
        return this;
    }

    public String getTopic() {
        return String.valueOf(getHeader(Headers.Topic));
    }

    public Message setTopic(String topic) {
        return setHeader(Headers.Topic, topic);
    }

    public Message addHeaders(List<Header> collect) {
        getHeaders().addAll(collect);
        return this;
    }

    public enum Headers {
        Topic,
        Partition,
        Offset,
        Timestamp,
        TimestampType,
        SerializedKeySize,
        SerializedValueSize,
        Key,
    }

    private static class HeaderMatcher implements Predicate<Header> {
        private final String name;

        private HeaderMatcher(String name) {
            this.name = name;
        }

        @Override
        public boolean test(Header header) {
            return header.getName().equals(name);
        }
    }

    @Accessors(chain = true)
    @Data
    public static class Header {
        String name;
        Object value;
    }
}
