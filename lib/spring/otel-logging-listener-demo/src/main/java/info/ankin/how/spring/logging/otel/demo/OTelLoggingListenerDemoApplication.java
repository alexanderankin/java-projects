package info.ankin.how.spring.logging.otel.demo;

import lombok.extern.slf4j.Slf4j;
import org.slf4j.event.Level;
import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

import java.util.Random;
import java.util.Timer;
import java.util.TimerTask;

@Slf4j
@SpringBootApplication
class OTelLoggingListenerDemoApplication {
    public static void main(String[] args) {
        SpringApplication.run(OTelLoggingListenerDemoApplication.class, args);
    }

    @Bean
    ApplicationRunner applicationRunner() {
        return args -> {
            log.info("hello world");

            new Timer().schedule(new TimerTask() {
                private final Random random = new Random();
                private final Level[] levels = new Level[]{Level.TRACE, Level.DEBUG, Level.INFO, Level.WARN, Level.ERROR};

                @Override
                public void run() {
                    var nextLevelIndex = random.nextInt(levels.length);
                    var nextLevel = levels[nextLevelIndex];
                    log.atLevel(nextLevel).log("hello world");
                }
            }, 0, 1000);
        };
    }
}
