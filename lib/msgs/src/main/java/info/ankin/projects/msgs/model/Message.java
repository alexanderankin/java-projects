package info.ankin.projects.msgs.model;

import lombok.Data;
import lombok.experimental.Accessors;

import java.util.List;

@Accessors(chain = true)
@Data
public class Message {
    String contents;
    List<Header> headers;

    @Accessors(chain = true)
    @Data
    public static class Header {
        String name;
        String value;
    }
}
