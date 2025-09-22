package info.ankin.how.spring.logging.otel.appender;

import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.AppenderBase;
import ch.qos.logback.core.encoder.Encoder;
import io.opentelemetry.api.OpenTelemetry;
import io.opentelemetry.api.common.Value;
import io.opentelemetry.api.common.ValueType;
import io.opentelemetry.api.logs.LogRecordBuilder;
import io.opentelemetry.api.logs.Logger;
import io.opentelemetry.api.logs.Severity;
import lombok.RequiredArgsConstructor;

import java.nio.charset.StandardCharsets;
import java.util.concurrent.atomic.AtomicBoolean;

import static ch.qos.logback.classic.Level.*;

@RequiredArgsConstructor
public class OpenTelemetryAppender extends AppenderBase<ILoggingEvent> {
    private final Encoder<ILoggingEvent> encoder;
    private final OpenTelemetry openTelemetry;
    private final int[] standardLogbackLevels = new int[]{ERROR_INT, WARN_INT, INFO_INT, DEBUG_INT, TRACE_INT};
    private final Severity[] severities = Severity.values();

    @Override
    protected void append(ILoggingEvent eventObject) {
        Logger logger = openTelemetry.getLogsBridge().get(eventObject.getLoggerName());
        LogRecordBuilder logRecordBuilder = logger.logRecordBuilder();
        logRecordBuilder.setTimestamp(eventObject.getInstant());

        Severity severity = getSeverity(eventObject);
        logRecordBuilder.setSeverity(severity);
        logRecordBuilder.setSeverityText(severity.name());

        // do not set attributes or context, simply reply on json formatter from logstash package
        logRecordBuilder.setBody(new EfficientUtf8StringByteArrayValue(encoder.encode(eventObject)));

        logRecordBuilder.emit();
    }

    Severity getSeverity(ILoggingEvent eventObject) {
        var level = eventObject.getLevel();
        if (level == null) {
            return Severity.INFO;
        }
        int logbackLevel = level.toInt();
        if (!isStandardLevel(logbackLevel)) {
            return Severity.UNDEFINED_SEVERITY_NUMBER;
        }

        int logbackLevelTenThousands = logbackLevel / 10000;
        int severity = 1 + (4 * logbackLevelTenThousands);
        return severities[severity];
    }

    private boolean isStandardLevel(int logbackLevel) {
        for (int standardLogbackLevel : standardLogbackLevels) {
            if (standardLogbackLevel == logbackLevel) {
                return true;
            }
        }
        return false;
    }

    private static class EfficientUtf8StringByteArrayValue implements Value<byte[]> {
        private final byte[] encoded;
        private final AtomicBoolean decodedAlready = new AtomicBoolean();
        private String decoded;

        public EfficientUtf8StringByteArrayValue(byte[] encode) {
            encoded = encode;
        }

        @Override
        public ValueType getType() {
            return ValueType.BYTES;
        }

        @Override
        public byte[] getValue() {
            return encoded;
        }

        @Override
        public String asString() {
            var previousValue = decodedAlready.getAndSet(true);
            if (!previousValue) {
                decoded = new String(encoded, StandardCharsets.UTF_8);
            }
            return decoded;
        }
    }
}
