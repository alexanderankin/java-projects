# `https-customizer` AutoConfiguration

This library will detect which server you are using and
register the appropriate customizer as a bean.

## Usage

> todo publish to maven central

First add this library to your application:

```groovy
dependencies {
    implementation platform('org.springframework.boot:spring-boot-dependencies:2.7.2')
    implementation 'info.ankin.projects:https-customizer:0.0.1-SNAPSHOT'
    // implementation 'org.springframework.boot:spring-boot-starter-webflux' // either or
    implementation 'org.springframework.boot:spring-boot-starter-web'
    implementation 'info.ankin.projects:https-customizer-autoconfigure:0.0.1-SNAPSHOT'
}
```

Then make sure to enable this behavior in your application configuration:

```yaml
spring:
  application:
    name: MySelfSignedApplication
https-customizer:
  enabled: true
```
