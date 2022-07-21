# `https-customizer`

hooks into the creation of a web server in Spring Boot and
configures it with a self-signed certificate.

## Configuration

```java
import java.util.Date;

public class HttpsCustomizerProperties {
    private static final long TEN_YEARS_IN_MILLISECONDS = 315569260000L;

    String fqdn = "localhost";
    Integer bits = 2048;
    Date notBefore = new Date();
    Date notAfter = new Date(notBefore.getTime() + TEN_YEARS_IN_MILLISECONDS);
    String algorithm = "RSA";
}
```

uses [netty's SelfSignedCertificate][1].

## Usage

> todo publish to maven central

this library is intended for use within a spring application
(e.g. one that imports the spring BOMs):

```groovy
dependencies {
    implementation platform('org.springframework.boot:spring-boot-dependencies:2.7.2')
    implementation 'info.ankin.projects:https-customizer:0.0.1-SNAPSHOT'
    implementation 'org.springframework.boot:spring-boot-starter-webflux' // uses netty
    implementation 'org.springframework.boot:spring-boot-starter-web' // servlet-based tomcat
}
```

Simply register the respective class for your webserver as a bean.

```java
@org.springframework.context.annotation.Configuration
public class Configuration {
    @org.springframework.context.annotation.Bean
    public NettyHttpsCustomizer nettyHttpsCustomizer() {
        return new NettyHttpsCustomizer(new HttpsCustomizerProperties().setBits(2048));
    }
}
```

Or use the accompanying autoconfiguration library
([https-customizer-autoconfigure](../https-customizer-autoconfigure)).

[1]: https://netty.io/4.0/api/io/netty/handler/ssl/util/SelfSignedCertificate.html
