package info.ankin.projects.spring.httpscustomizer;

import org.springframework.boot.web.embedded.tomcat.TomcatReactiveWebServerFactory;

public class TomcatReactiveHttpsCustomizer extends HttpsCustomizer<TomcatReactiveWebServerFactory> {
    @Override
    public void customize(TomcatReactiveWebServerFactory factory) {
        super.customize(factory);
    }
}
