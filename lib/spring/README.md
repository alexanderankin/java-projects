project status:

| name                           | status         |
|--------------------------------|----------------|
| https-customizer               | useful         |
| https-customizer-autoconfigure | useful         |
| json-logging-listener          | useful         |
| otel-logback-appender          | in development |
| otel-logging-listener          | researching    |

The HTTPS customizer as of yet does not have an equivalent in Spring Boot.

The JSON Logging functionality is now present in Spring Boot, but I prefer my version

Open Telemetry is a technology I have not had to fully reckon with yet,
so those libraries are still a work in progress.
The listener component in particular may need significant re-imagining (based on OTel's own auto-config).
