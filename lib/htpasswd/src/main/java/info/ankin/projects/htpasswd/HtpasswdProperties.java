package info.ankin.projects.htpasswd;

import lombok.Data;
import lombok.experimental.Accessors;

@Accessors(chain = true)
@Data
public class HtpasswdProperties {
    int bcryptCost = 12;
}
