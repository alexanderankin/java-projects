package info.ankin.projects.gradle.plugins.cac;

import info.ankin.projects.gradle.plugins.cac.CodeArtifactCredsExtension.Auth;
import lombok.Data;
import lombok.SneakyThrows;
import lombok.experimental.Accessors;
import org.gradle.api.Plugin;
import org.gradle.api.Project;
import org.gradle.api.artifacts.repositories.ArtifactRepository;
import org.gradle.api.artifacts.repositories.MavenArtifactRepository;

import javax.annotation.Nonnull;
import java.io.File;
import java.io.IOException;
import java.nio.channels.FileChannel;
import java.nio.channels.FileLock;
import java.nio.channels.OverlappingFileLockException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.time.Duration;
import java.util.*;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

/**
 * the plugin
 */
public class CodeArtifactCredsPlugin implements Plugin<Project> {

    static final Pattern CODE_ARTIFACT_MAVEN_PATTERN = Pattern.compile(
            // domain and account
            "https://(\\w{1,200})-(\\d{12})" +
                    "\\.d\\.codeartifact\\." +
                    // region
                    "([\\w-]{1,50})\\." +
                    // repo name
                    "amazonaws\\.com/maven/(\\w{1,200})/?");
    static final Predicate<String> CODE_ARTIFACT_MAVEN_PATTERN_MATCH_PREDICATE =
            CODE_ARTIFACT_MAVEN_PATTERN.asMatchPredicate();

    Path awsCliPath = Cache.which("aws");
    Project project;
    RegionCache regionCache;
    AuthTokenCache authTokenCache;
    AccountCache accountCache;
    CodeArtifactCredsExtension extension;

    @Override
    public void apply(@Nonnull Project target) {
        this.project = target;
        project.getLogger().lifecycle("installing CodeArtifactCredsPlugin plugin");
        regionCache = new RegionCache(this);
        authTokenCache = new AuthTokenCache(this);
        accountCache = new AccountCache(this);

        extension = target.getObjects().newInstance(CodeArtifactCredsExtension.class, this);
        target.getExtensions().add(CodeArtifactCredsExtension.class, "codeArtifactCredentials", extension);

        target.afterEvaluate(this::fixRepoCreds);
    }

    Path handleAwsCliPath() {
        Optional<Path> fromExtension = Optional.ofNullable(extension.getAwsCliPath())
                .filter(Predicate.not(String::isEmpty))
                .map(Path::of)
                .map(Path::toFile)
                .filter(File::exists)
                .map(File::toPath);

        if (fromExtension.isPresent()) {
            project.getLogger().lifecycle("CodeArtifactCredsPlugin: aws cli provided and exists");
            awsCliPath = fromExtension.get();
        } else {
            // we initialize this early for util usage
            project.getLogger().lifecycle("CodeArtifactCredsPlugin: aws cli not provided, using default from $PATH: {}", awsCliPath);
        }

        return awsCliPath;
    }

    void fixRepoCreds(Project project) {
        awsCliPath = handleAwsCliPath();
        project.getLogger().lifecycle("CodeArtifactCredsPlugin: found aws command: {}", awsCliPath);

        // goThroughList();
        autodetect();
    }

    void autodetect() {
        List<MavenArtifactRepository> codeArtifactRepos = project.getRepositories().stream()
                .filter(MavenArtifactRepository.class::isInstance)
                .map(MavenArtifactRepository.class::cast)
                .filter(m -> CODE_ARTIFACT_MAVEN_PATTERN_MATCH_PREDICATE.test(m.getUrl().toString()))
                .toList();

        List<String> names = codeArtifactRepos.stream().map(ArtifactRepository::getName).toList();
        project.getLogger().lifecycle("CodeArtifactCredsPlugin: fixing up repo creds, found matching repos: {}", names);

        List<MavenArtifactRepository> withoutPassword = codeArtifactRepos.stream().filter(e -> e.getCredentials().getPassword() == null).toList();
        project.getLogger().lifecycle("CodeArtifactCredsPlugin: matching repos without passwords: {}", withoutPassword);

        // information we need to look things up by
        Map<MavenArtifactRepository, String> repoToDomain = withoutPassword.stream()
                .map(e -> {
                    Matcher matcher = CODE_ARTIFACT_MAVEN_PATTERN.matcher(e.getUrl().toString());
                    if (!matcher.matches()) return null;
                    return Map.entry(e, matcher.group(1));
                })
                .filter(Objects::nonNull)
                .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));
        project.getLogger().lifecycle("CodeArtifactCredsPlugin: matching repos without passwords: {}", withoutPassword);

        // look them up once
        Map<String, String> domainToKey = repoToDomain.values().stream().distinct()
                .map(d -> Map.entry(d, getToken(d)))
                .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));

        // put it all together
        withoutPassword.forEach(repo -> {
            repo.getCredentials().setUsername("aws");
            repo.getCredentials().setPassword(domainToKey.get(repoToDomain.get(repo)));
        });
    }

    private String getToken(String d) {
        return authTokenCache.valueFor(new AuthTokenKey().setAuth(extension.getAuth()).setDomain(d));
    }

    // void goThroughList() {
    //     Set<URI> repos = Set.of(URI.create(""));
    //     project.getLogger().lifecycle("CodeArtifactCredsPlugin: repos we know about are", repos);
    //
    //     List<MavenArtifactRepository> mavenRepos = project.getRepositories().stream()
    //             .filter(MavenArtifactRepository.class::isInstance)
    //             .map(MavenArtifactRepository.class::cast)
    //             .toList();
    //
    //     Object mavenRepoIds = mavenRepos.stream().map(MavenArtifactRepository::getName).toList();
    //     project.getLogger().lifecycle("CodeArtifactCredsPlugin: repos found in the project", mavenRepoIds);
    //
    //     for (MavenArtifactRepository mavenRepo : mavenRepos) {
    //         // Set<URI> artifactUrls = mavenRepo.getArtifactUrls();
    //         URI url = mavenRepo.getUrl();
    //
    //         boolean shouldFix = url.getScheme().startsWith("http") &&
    //                 repos.contains(url) &&
    //                 mavenRepo.getCredentials().getPassword() == null;
    //
    //         project.getLogger().lifecycle(
    //                 "CodeArtifactCredsPlugin: considering mavenRepo {} should fix: {} (url = {})",
    //                 mavenRepo.getName(),
    //                 shouldFix,
    //                 mavenRepo.getUrl());
    //         if (!shouldFix) continue;
    //
    //         mavenRepo.credentials(passwordCredentials -> {
    //             passwordCredentials.setUsername("aws");
    //             passwordCredentials.setPassword(
    //                     authTokenCache.valueFor(
    //                             this.project.getExtensions()
    //                                     .getByType(CodeArtifactCredsExtension.class)
    //                                     .getAuth()
    //                     )
    //             );
    //         });
    //
    //     }
    // }

    // Set<URI> determineRepos() {
    //     CodeArtifactCredsExtension extension =
    //             project.getExtensions().getByType(CodeArtifactCredsExtension.class);
    //
    //     Repositories codeRepositories = extension.getCodeRepositories();
    //     URI defaultUri;
    //     try {
    //         defaultUri = getUri(codeRepositories.getDefaultRepository(), extension.getAuth());
    //     } catch (Exception ignored) {
    //         // default one is allowed to be broken
    //         defaultUri = null;
    //     }
    //
    //     return Stream.of(
    //                     // default one
    //                     defaultUri == null ? Stream.<URI>of() : Stream.of(defaultUri),
    //                     // other ones
    //                     codeRepositories.getAliases().stream()
    //                             .map(e -> getUri(e, extension.getAuth()))
    //             )
    //             .flatMap(Function.identity())
    //             .collect(Collectors.toSet());
    // }

    // private @NotNull URI getUri(Repository defaultRepository, Auth auth) {
    //     Property<String> url = defaultRepository.getUrl();
    //     if (url.isPresent())
    //         return URI.create(url.get());
    //     String awsCodeDomain = defaultRepository.getConfig().getAwsCodeDomain().getOrNull();
    //     String awsCodeRepository = defaultRepository.getConfig().getAwsCodeRepository().getOrNull();
    //     String awsCodeRegion = defaultRepository.getConfig().getAwsCodeRegion().getOrNull();
    //     String awsCodeAccount = defaultRepository.getConfig().getAwsCodeAccount().getOrNull();
    //
    //
    //     List<String> errors = new ArrayList<>();
    //     if (awsCodeDomain == null) errors.add("awsCodeDomain cannot be null");
    //     if (awsCodeRepository == null) errors.add("awsCodeRepository cannot be null");
    //
    //     if (awsCodeAccount == null)
    //         awsCodeAccount = accountCache.valueFor(auth);
    //     if (awsCodeRegion == null)
    //         awsCodeRegion = regionCache.valueFor(auth);
    //     if (awsCodeRegion == null)
    //         errors.add("awsCodeRegion cannot be null");
    //
    //     if (!errors.isEmpty()) {
    //         String name;
    //         if (defaultRepository instanceof NamedRepository namedRepository) {
    //             name = namedRepository.getName();
    //         } else {
    //             name = "default";
    //         }
    //         throw new RuntimeException("Errors for repository '" + name + "':\n" + String.join("\n", errors));
    //     }
    //     return URI.create("https://" + awsCodeDomain + "-" + awsCodeAccount + ".d.codeartifact." + awsCodeRegion + ".amazonaws.com/maven/" + awsCodeRepository + "/");
    // }

    static class InvalidEnvironmentException extends RuntimeException {
        public InvalidEnvironmentException(String message) {
            super("Something is wrong with this system: " + message);
        }
    }

    static class AwsCliException extends RuntimeException {
        public AwsCliException(String message) {
            super("Something went wrong with the aws cli: " + message);
        }
    }

    private abstract static class Cache<T> {
        static final Map<String, String> AWS_PREFIXED_VARS =
                System.getenv().entrySet().stream()
                        .filter(e -> e.getKey().startsWith("AWS_"))
                        .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));
        protected final Project project;

        private Cache(Project project) {
            this.project = project;
        }

        @SuppressWarnings("SameParameterValue")
        static Path which(String command) {
            String path = System.getenv("PATH");
            if (path == null || path.isBlank())
                throw new InvalidEnvironmentException("env var PATH is missing");
            String[] directories = path.split(File.pathSeparator);

            for (String directory : directories) {
                File executable = new File(directory, command);
                if (executable.exists() && !executable.isDirectory()) {
                    return executable.toPath();
                }
            }

            return null;
        }

        @SneakyThrows
        protected String runCommand(Auth auth, List<String> command) {
            ProcessBuilder processBuilder = new ProcessBuilder(command);

            processBuilder.environment().put("PATH", System.getenv("PATH"));
            processBuilder.environment().putAll(AWS_PREFIXED_VARS);
            if (auth.getAwsProfile().isPresent()) {
                processBuilder.environment().put("AWS_PROFILE", auth.getAwsProfile().get());
            }

            project.getLogger().lifecycle("CodeArtifactCredsPlugin.Cache running cmd {} with env {} (AWS_* env {})", command, processBuilder.environment(), AWS_PREFIXED_VARS);
            Process aws = processBuilder.start();
            int exit = aws.waitFor();
            if (exit != 0)
                throw new AwsCliException("command " + command + " exited with " + exit);

            String result = new String(aws.getInputStream().readAllBytes(), StandardCharsets.UTF_8).strip();
            project.getLogger().lifecycle("CodeArtifactCredsPlugin.Cache result is: {}", result);
            return result;
        }

        public abstract String valueFor(T auth);
    }

    // cheap operation
    static class RegionCache extends Cache<Auth> {
        private final Map<Auth, String> cache = Collections.synchronizedMap(new HashMap<>());
        private final CodeArtifactCredsPlugin plugin;

        private RegionCache(CodeArtifactCredsPlugin plugin) {
            super(plugin.project);
            this.plugin = plugin;
        }

        public String valueFor(Auth auth) {
            return cache.computeIfAbsent(auth, a -> runCommand(a, List.of(plugin.awsCliPath.toString(), "configure", "get", "region")));
        }
    }

    private static abstract class ExpensiveCache<T> extends Cache<T> {
        private static final Path FOLDER = Path.of(System.getProperty("user.home"), ".gradle")
                .resolve(CodeArtifactCredsPlugin.class.getName());

        static {
            // noinspection ResultOfMethodCallIgnored
            FOLDER.toFile().mkdirs();
        }

        private ExpensiveCache(Project project) {
            super(project);
        }

        @SneakyThrows
        protected AutoCloseable lock(String name) {
            return new CacheLock(project, name, getClass().getSimpleName());
        }

        protected boolean validate(String value) {
            return true;
        }

        @SneakyThrows
        protected String useCache(Auth auth, String cacheKey, String storageKey, Function<Auth, String> function) {
            try (AutoCloseable ignored = lock(cacheKey)) {
                String read = read(storageKey);
                if (read != null && validate(read))
                    return read;

                String newValue = function.apply(auth);
                write(storageKey, newValue);
                return newValue;
            }
        }

        protected String read(String name) {
            Path path = FOLDER.resolve(name);
            try {
                String result = Files.readString(path, StandardCharsets.UTF_8);
                project.getLogger().lifecycle("CodeArtifactCredsPlugin.ExpensiveCache({}): read returned {} for path {}",
                        getClass().getSimpleName(), result, path);
                return result;
            } catch (IOException e) {
                project.getLogger().lifecycle("CodeArtifactCredsPlugin.ExpensiveCache({}): failed to read {}: {}",
                        getClass().getSimpleName(), path, e.getMessage());
                return null;
            }
        }

        @SneakyThrows
        protected void write(String name, String value) {
            Path path = FOLDER.resolve(name);
            project.getLogger().lifecycle("CodeArtifactCredsPlugin.ExpensiveCache({}): writing {} to path {}",
                    getClass().getSimpleName(), value, path);
            Files.writeString(path, value, StandardCharsets.UTF_8);
        }

        // https://stackoverflow.com/a/48817873/4971476
        static class CacheLock implements AutoCloseable {
            private static final long TEN_SEC_IN_NANO = Duration.ofSeconds(10).toNanos();
            private final FileChannel channel;
            private final FileLock fileLock;
            private final Project project;
            private final Path path;
            private final String tag;

            @SneakyThrows
            CacheLock(Project project, String name, String tag) {
                this.project = project;
                path = FOLDER.resolve(name);
                this.tag = tag;
                channel = FileChannel.open(path, StandardOpenOption.CREATE, StandardOpenOption.WRITE);
                project.getLogger().lifecycle("CodeArtifactCredsPlugin.ExpensiveCache({}): locking {}", tag, path);
                long start = System.nanoTime();
                FileLock lock = null;
                Exception last = null;
                while (System.nanoTime() < (start + TEN_SEC_IN_NANO)) {
                    try {
                        lock = channel.tryLock();
                        // noinspection BusyWait
                        Thread.sleep(100);
                        Thread.onSpinWait();
                        break;
                    } catch (OverlappingFileLockException ignored) {
                        // means we are waiting on another thread
                    } catch (Exception e) {
                        last = e;
                    }
                }

                if (lock == null) {
                    Exception e = new InvalidEnvironmentException("could not lock on " + path);
                    if (last != null) e.initCause(last);
                    throw e;
                }
                fileLock = lock;
            }

            @Override
            public void close() {
                project.getLogger().lifecycle("CodeArtifactCredsPlugin.ExpensiveCache({}): unlocking {}", tag, path);
                try {
                    fileLock.close();
                } catch (IOException ignored) {
                    project.getLogger().lifecycle("CodeArtifactCredsPlugin.ExpensiveCache({}): fileLock {}", tag, path, ignored);
                }

                try {
                    channel.close();
                } catch (IOException ignored) {
                    project.getLogger().lifecycle("CodeArtifactCredsPlugin.ExpensiveCache({}): channel {}", tag, path, ignored);
                }
            }
        }
    }

    @Data
    @Accessors(chain = true)
    static class AuthTokenKey {
        Auth auth;
        String domain;
    }

    // expensive/hazardous operation
    static class AuthTokenCache extends ExpensiveCache<AuthTokenKey> {
        private final CodeArtifactCredsPlugin plugin;

        private AuthTokenCache(CodeArtifactCredsPlugin plugin) {
            super(plugin.project);
            this.plugin = plugin;
        }

        @SneakyThrows
        public String valueFor(AuthTokenKey authTokenKey) {
            Auth auth = authTokenKey.getAuth();
            String cacheKey = auth.getAwsProfile().getOrElse("default");
            String storageKey = cacheKey + ".auth";
            //noinspection SpellCheckingInspection
            return useCache(auth, cacheKey, storageKey, a -> runCommand(a, List.of(plugin.awsCliPath.toString(),
                    "codeartifact",
                    "get-authorization-token",
                    "--domain",
                    authTokenKey.getDomain(),
                    // consider parsing for 'expiration' also
                    "--query",
                    "authorizationToken",
                    "--output",
                    "text")));
        }

        @Override
        protected boolean validate(String value) {
            String[] parts = value.trim().split("\\.");
            if (parts.length < 1) {
                project.getLogger().lifecycle("token has no parts when split by dot: '{}'", parts.length);
                return false;
            }

            byte[] decoded;
            String firstPart = parts[0];
            try {
                decoded = Base64.getUrlDecoder().decode(firstPart);
            } catch (Exception e) {
                project.getLogger().lifecycle("could not base64 decode '{}', so token is not valid", firstPart);
                return false;
            }

            TokenHeader tokenHeader = TokenHeader.parse(new String(decoded));
            if (tokenHeader == null) {
                project.getLogger().lifecycle("could not parse token header from firstPart");
                return false;
            }

            long tokenExp = tokenHeader.getExp() * 1000L;
            long now = System.currentTimeMillis();
            boolean valid = now < tokenExp;
            project.getLogger().lifecycle("token is valid: {} (token expires at {}, it is {})", valid, tokenExp, now);
            return valid;
        }

        @SuppressWarnings("CommentedOutCode")
        @Data
        @Accessors(chain = true)
        static class TokenHeader {
            // int ver;
            // int isu; // unix issued at
            // String enc;
            // String tag;
            int exp;
            // String alg;
            // String iv;

            static TokenHeader parse(String json) {
                // https://stackoverflow.com/a/43606476/4971476
                String expString = Pattern.compile(".{0,1000}\"exp\"\\s{0,1000}:\\s{0,1000}(\\d{0,20}).{0,1000}")
                        .matcher(json)
                        .results()
                        .map(m -> m.group(1))
                        .findFirst()
                        .orElse(null);

                if (expString == null) return null;
                int exp = Integer.parseInt(expString);
                return new TokenHeader().setExp(exp);
            }
        }
    }

    // expensive/hazardous operation
    // BUT - we have no way to invalidate :(
    static class AccountCache extends Cache<Auth> {
        // at least in memory per project
        private final Map<Auth, String> cache = new HashMap<>();
        private final CodeArtifactCredsPlugin plugin;

        private AccountCache(CodeArtifactCredsPlugin plugin) {
            super(plugin.project);
            this.plugin = plugin;
        }

        public synchronized String valueFor(Auth auth) {
            String c = cache.get(auth);
            if (c != null) return c;
            String n = doGetValue(auth);
            cache.put(auth, n);
            return n;
            // String cacheKey = auth.getAwsProfile().getOrElse("default");
            // String storageKey = cacheKey + ".account";
            // return useCache(auth, cacheKey, storageKey, this::doGetValue);
        }

        private String doGetValue(Auth auth) {
            String newValue = runCommand(auth, List.of(plugin.awsCliPath.toString(),
                    "sts",
                    "get-caller-identity",
                    "--query",
                    "Account",
                    "--output",
                    "text"));
            if (newValue.length() != 12)
                throw new AwsCliException("account number was not 12 digits: " + newValue);
            return newValue;
        }
    }
}
