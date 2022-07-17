package info.ankin.projects.cli.messagebus.service;

import info.ankin.projects.cli.messagebus.config.SettingsProperties;
import info.ankin.projects.cli.messagebus.exception.MissingConfigurationException;
import info.ankin.projects.cli.messagebus.mixin.ConnectionInfo;
import info.ankin.projects.cli.messagebus.model.BrokerInformation;
import info.ankin.projects.cli.messagebus.model.BrokerType;
import info.ankin.projects.cli.messagebus.model.Settings;
import io.micronaut.serde.ObjectMapper;
import jakarta.inject.Singleton;
import lombok.Getter;
import lombok.SneakyThrows;
import org.apache.commons.io.FileUtils;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.function.Supplier;

@Singleton
public class SettingsService {
    private final SettingsProperties settingsProperties;
    private final ObjectMapper objectMapper;
    private final Path settingsFile;
    @Getter
    private final Settings settings;

    @SneakyThrows
    public SettingsService(SettingsProperties settingsProperties, ObjectMapper objectMapper) {
        this.settingsProperties = settingsProperties;
        this.objectMapper = objectMapper;
        settingsFile = Paths.get(FileUtils.getUserDirectoryPath())
                .resolve(Paths.get(settingsProperties.getConfig()));
        if (settingsFile.toFile().exists() && settingsFile.toFile().isFile()) {
            String s = Files.readString(settingsFile);
            settings = objectMapper.readValue(s, Settings.class);
        } else {
            settings = new Settings();
        }
    }

    public Mono<Void> persist() {
        return Mono.<Void>fromRunnable(this::persistSync).subscribeOn(Schedulers.boundedElastic());
    }

    @SneakyThrows
    private void persistSync() {
        Files.writeString(settingsFile, objectMapper.writeValueAsString(settings));
    }

    public BrokerInformation overlay(ConnectionInfo input) {
        BrokerType bt = getOrComplain("Could not determine broker type", input::getBrokerType, settings::getType);
        return new BrokerInformation(
                bt,
                input.getCredentials().getHost(),
                input.getCredentials().getVirtualHost(),
                input.getCredentials().determineUsername(),
                input.getCredentials().determinePassword()
        );
    }

    @SafeVarargs
    @SuppressWarnings("SameParameterValue")
    private <T> T getOrComplain(String message, Supplier<T>...supplier) {
        for (Supplier<T> s : supplier) {
            T t = s.get();
            if (t != null) {
                return t;
            }
        }

        throw new MissingConfigurationException(message);
    }
}
