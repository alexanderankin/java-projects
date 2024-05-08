package info.ankin.projects.gradle.plugins.cac;

import lombok.Getter;
import org.gradle.api.Action;
import org.gradle.api.provider.Property;
import org.gradle.api.tasks.Nested;

import javax.inject.Inject;
import java.nio.file.Path;

@SuppressWarnings("CommentedOutCode")
public abstract class CodeArtifactCredsExtension {
    private final CodeArtifactCredsPlugin plugin;

    @Getter
    private String awsCliPath;

    @Inject
    public CodeArtifactCredsExtension(CodeArtifactCredsPlugin plugin) {
        this.plugin = plugin;
    }

    public void setAwsCliPath(String awsCliPath) {
        plugin.awsCliPath = Path.of(awsCliPath);
        this.awsCliPath = awsCliPath;
    }

    public void awsCliPath(String awsCliPath) {
        setAwsCliPath(awsCliPath);
    }

    public String awsAccount() {
        return plugin.accountCache.valueFor(getAuth());
    }

    // /**
    //  * the plugin will apply credentials for any repo matching the pattern
    //  *
    //  * @return if this is enabled
    //  */
    // abstract public Property<Boolean> getAutoDetect();
    //
    // public void autoDetect(Boolean autoDetect) {
    //     getAutoDetect().set(autoDetect);
    // }

    // import groovy.lang.Closure;
    // import org.gradle.api.Named;
    // import org.gradle.api.NamedDomainObjectContainer;
    // @Nested
    // abstract public Repositories getCodeRepositories();
    //
    // public void codeRepositories(Action<Repositories> action) {
    //     action.execute(getCodeRepositories());
    // }

    /**
     * we are using the aws sdk/cli so this just sets env vars (for calling aws cli)
     */
    @Nested
    abstract public Auth getAuth();

    public void auth(Action<Auth> action) {
        action.execute(getAuth());
    }

    public abstract static class Auth {
        /**
         * sets {@code AWS_PROFILE} when calling aws cli
         */
        abstract public Property<String> getAwsProfile();
    }
    //
    // public abstract static class Repositories {
    //     @Nested
    //     abstract public Repository getDefaultRepository();
    //
    //     public void defaultRepository(Action<Repository> action) {
    //         action.execute(getDefaultRepository());
    //     }
    //
    //     @Nested
    //     abstract public NamedDomainObjectContainer<NamedRepository> getAliases();
    //
    //     public void aliases(Closure<?> action) {
    //         getAliases().configure(action);
    //     }
    //
    //     public void aliases(Action<NamedDomainObjectContainer<NamedRepository>> action) {
    //         action.execute(getAliases());
    //     }
    // }
    //
    // public static abstract class Repository {
    //     /**
    //      * takes priority over config
    //      */
    //     abstract public Property<String> getUrl();
    //
    //     /**
    //      * build up the url with these inputs if you don't provide the url
    //      */
    //     @Nested
    //     abstract public Config getConfig();
    //
    //     public void url(String value) {
    //         getUrl().set(value);
    //     }
    //
    //     public void config(Action<Config> config) {
    //         config.execute(getConfig());
    //     }
    //
    //     public static abstract class Config {
    //         abstract public Property<String> getAwsCodeAccount();
    //
    //         abstract public Property<String> getAwsCodeRegion();
    //
    //         abstract public Property<String> getAwsCodeDomain();
    //
    //         abstract public Property<String> getAwsCodeRepository();
    //     }
    // }
    //
    // /**
    //  * @see <a href=https://docs.gradle.org/current/userguide/custom_gradle_types.html#ex-managing-a-collection-of-objects>custom_gradle_types.html: "Type must have a read-only 'name' property"</a>
    //  */
    // public abstract static class NamedRepository extends Repository implements Named {
    // }
}
