package info.ankin.projects.git.httpbackend.old;

import lombok.Data;
import lombok.experimental.Accessors;
import lombok.extern.slf4j.Slf4j;

@Accessors(chain = true)
@Data
@Slf4j
public class GitService {
    boolean info;
    String name;
    Object opts;
}
