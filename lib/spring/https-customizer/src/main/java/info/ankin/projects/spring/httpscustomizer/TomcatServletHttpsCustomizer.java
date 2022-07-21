package info.ankin.projects.spring.httpscustomizer;

import org.springframework.boot.web.embedded.tomcat.TomcatServletWebServerFactory;

public class TomcatServletHttpsCustomizer extends HttpsCustomizer<TomcatServletWebServerFactory> {
    @Override
    public void customize(TomcatServletWebServerFactory factory) {
        super.customize(factory);
    }
}
