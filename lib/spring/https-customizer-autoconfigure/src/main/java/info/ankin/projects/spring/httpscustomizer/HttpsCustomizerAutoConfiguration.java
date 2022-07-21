package info.ankin.projects.spring.httpscustomizer;

import info.ankin.projects.spring.httpscustomizer.properties.HttpsCustomizerConfigurationProperties;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.web.embedded.netty.NettyReactiveWebServerFactory;
import org.springframework.boot.web.embedded.tomcat.TomcatReactiveWebServerFactory;
import org.springframework.boot.web.embedded.tomcat.TomcatServletWebServerFactory;
import org.springframework.context.annotation.Bean;

@ConditionalOnProperty("https-customizer.enabled")
@AutoConfiguration
@EnableConfigurationProperties(HttpsCustomizerConfigurationProperties.class)
public class HttpsCustomizerAutoConfiguration {
    @Autowired
    HttpsCustomizerConfigurationProperties customizerConfigurationProperties;

    @ConditionalOnBean(NettyReactiveWebServerFactory.class)
    @Bean
    public NettyHttpsCustomizer nettyHttpsCustomizer() {
        return new NettyHttpsCustomizer(customizerConfigurationProperties.toProps());
    }

    @ConditionalOnBean(TomcatReactiveWebServerFactory.class)
    @Bean
    public TomcatReactiveHttpsCustomizer tomcatReactiveHttpsCustomizer() {
        return new TomcatReactiveHttpsCustomizer(customizerConfigurationProperties.toProps());
    }

    @ConditionalOnBean(TomcatServletWebServerFactory.class)
    @Bean
    public TomcatServletHttpsCustomizer tomcatServletHttpsCustomizer() {
        return new TomcatServletHttpsCustomizer(customizerConfigurationProperties.toProps());
    }
}
