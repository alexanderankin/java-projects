package info.ankin.projects.spring.httpscustomizer;

import org.springframework.boot.web.embedded.netty.NettyReactiveWebServerFactory;

public class NettyHttpsCustomizer extends HttpsCustomizer<NettyReactiveWebServerFactory> {
    @Override
    public void customize(NettyReactiveWebServerFactory factory) {
        super.customize(factory);
    }
}
