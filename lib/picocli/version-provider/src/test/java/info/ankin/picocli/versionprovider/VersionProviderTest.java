package info.ankin.picocli.versionprovider;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class VersionProviderTest {

    Package p;
    VersionProvider versionProvider;

    @BeforeEach
    void setUp() {
        p = mock(Package.class);
        versionProvider = new TestVersionProvider(p);
    }

    @Test
    void emptyBehavior() {
        List<String> version = List.of(versionProvider.getVersion());
        assertThat(version, is(empty()));
    }

    @Test
    void test_returnsMockedVersion() {
        when(p.getImplementationVersion()).thenReturn("v1");
        List<String> version = List.of(versionProvider.getVersion());
        assertThat(version, hasSize(1));
        assertThat(version.get(0), is("v1"));
    }

    @Test
    void test_precedence() {
        when(p.getSpecificationVersion()).thenReturn("s1");
        {
            List<String> version = List.of(versionProvider.getVersion());
            assertThat(version, hasSize(1));
            assertThat(version.get(0), is("s1"));
        }
        when(p.getImplementationVersion()).thenReturn("v1");
        {
            List<String> version = List.of(versionProvider.getVersion());
            assertThat(version, hasSize(1));
            assertThat(version.get(0), is("v1"));
        }
    }

    @Test
    void test_failsWhenNoJar() {
        assertThat(List.of(new VersionProvider().getVersion()), is(empty()));
    }

    public static class TestVersionProvider extends VersionProvider {
        private final Package p;

        public TestVersionProvider(Package p) {
            this.p = p;
        }

        @Override
        protected Package pkg() {
            return p;
        }
    }

}
