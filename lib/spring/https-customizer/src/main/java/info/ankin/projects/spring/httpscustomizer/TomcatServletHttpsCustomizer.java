package info.ankin.projects.spring.httpscustomizer;

import org.springframework.boot.tomcat.servlet.TomcatServletWebServerFactory;

public class TomcatServletHttpsCustomizer extends HttpsCustomizer<TomcatServletWebServerFactory> {
    public TomcatServletHttpsCustomizer(HttpsCustomizerProperties properties) {
        super(properties);
    }

    @Override
    public void customize(TomcatServletWebServerFactory factory) {
        super.customize(factory);
    }
}
