package info.ankin.projects.supports_hyperlinks;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;

@Slf4j
class SupportsHyperlinksTest {

    @Test
    void usage() {
        boolean result = SupportsHyperlinks.checkerCached().supportsHyperlinks();
        log.info("we {}support hyperlinks", result ? "" : "do not ");
        log.info("the checker looks like this: {}", SupportsHyperlinks.checkerCached());
    }

}
