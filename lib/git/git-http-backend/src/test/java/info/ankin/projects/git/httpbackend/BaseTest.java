package info.ankin.projects.git.httpbackend;

import lombok.SneakyThrows;

import java.nio.file.FileSystem;
import java.nio.file.Path;
import java.nio.file.ProviderNotFoundException;
import java.nio.file.spi.FileSystemProvider;
import java.util.Map;
import java.util.Objects;
import java.util.ServiceLoader;

@SuppressWarnings("SameParameterValue")
public class BaseTest {
    @SneakyThrows
    protected FileSystem newFileSystemFromResource(String name) {
        return newFileSystem(Path.of(Objects.requireNonNull(getClass().getResource(name)).toURI()));
    }

    FileSystem newFileSystem(Path path) {
        return newFileSystem(path, Map.of(), null);
    }

    // copied from jre 17
    @SneakyThrows
    FileSystem newFileSystem(Path path, Map<String, ?> env, ClassLoader loader) {
        // check installed providers
        for (FileSystemProvider provider : FileSystemProvider.installedProviders()) {
            try {
                return provider.newFileSystem(path, env);
            } catch (UnsupportedOperationException ignored) {
            }
        }

        // if not found, use service-provider loading facility
        if (loader != null) {
            var sl = ServiceLoader.load(FileSystemProvider.class, loader);
            for (FileSystemProvider provider : sl) {
                try {
                    return provider.newFileSystem(path, env);
                } catch (UnsupportedOperationException ignored) {
                }
            }
        }

        throw new ProviderNotFoundException("Provider not found");
    }
}
