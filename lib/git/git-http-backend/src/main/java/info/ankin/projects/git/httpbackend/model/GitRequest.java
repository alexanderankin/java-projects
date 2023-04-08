package info.ankin.projects.git.httpbackend.model;

import lombok.Data;
import lombok.experimental.Accessors;

import java.net.URI;

@Accessors(chain = true)
@Data
public class GitRequest {
    /**
     * original copy of {@link Request}
     */
    final Request request;

    /**
     * parsed url from {@link #request}
     */
    URI uri;
}
