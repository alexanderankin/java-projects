package info.ankin.how.spring.logging.otel.appender;

import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.spi.LoggingEvent;
import io.opentelemetry.api.logs.Severity;
import io.opentelemetry.exporter.otlp.logs.OtlpGrpcLogRecordExporter;
import io.opentelemetry.sdk.OpenTelemetrySdk;
import io.opentelemetry.sdk.logs.LogRecordProcessor;
import io.opentelemetry.sdk.logs.SdkLoggerProvider;
import io.opentelemetry.sdk.logs.export.BatchLogRecordProcessor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;

class OpenTelemetryAppenderTest {
    OpenTelemetryAppender appender;

    @BeforeEach
    void setUp() {
        @SuppressWarnings("ConstantValue")
        LogRecordProcessor processor = 2 > 1
                ? LogRecordProcessor.composite()
                : BatchLogRecordProcessor.builder(
                OtlpGrpcLogRecordExporter.builder()
                        .setEndpoint("http://your-collector:4317")
                        .build()
        ).build();

        SdkLoggerProvider loggerProvider = SdkLoggerProvider.builder()
                .addLogRecordProcessor(processor)
                .build();

        var openTelemetry = OpenTelemetrySdk.builder()
                .setLoggerProvider(loggerProvider)
                .build();

        appender = new OpenTelemetryAppender(null, openTelemetry);
    }

    @ParameterizedTest
    @CsvSource({
            "ERROR,ERROR",
            "WARN,WARN",
            "INFO,INFO",
            "DEBUG,DEBUG",
            "TRACE,TRACE",
            "OFF,UNDEFINED_SEVERITY_NUMBER",
    })
    void testLoggingLevelMapping(String levelString, String expectedSeverity) {
        assertThat(appender.getSeverity(withLevel(Level.toLevel(levelString))), is(equalTo(Severity.valueOf(expectedSeverity))));
    }

    LoggingEvent withLevel(Level level) {
        LoggingEvent loggingEvent = new LoggingEvent();
        loggingEvent.setLevel(level);
        return loggingEvent;
    }
}
