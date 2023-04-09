package info.ankin.projects.git.httpbackend.old.model;

import lombok.Data;
import lombok.experimental.Accessors;

import java.io.InputStream;
import java.io.OutputStream;

@Accessors(chain = true)
@Data
public class Request {
    /**
     * full url from the original request
     */
    String url;

    String method;

    InputStream inputStream;

    /**
     * where to write the output to
     */
    OutputStream outputStream;
}
