package info.ankin.projects.gradle.plugins.cac;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import java.util.regex.Matcher;

import static org.junit.jupiter.api.Assertions.*;

class CodeArtifactCredsPluginTest {

    @ParameterizedTest
    @CsvSource({
            "https://repository-000000000000.d.codeartifact.us-east-1.amazonaws.com/maven/repo/,true,repository,000000000000,us-east-1,repo",
            "https://repository-000000000000.d.codeartifact.us-east-1.amazonaws.com/maven/repo,true,repository,000000000000,us-east-1,repo",
            "https://repository-000000000000.d.codeartifact.us-east-2.amazonaws.com/maven/bar/,true,repository,000000000000,us-east-2,bar",
            "https://repository-000000000000.d.codeartifact.us-east-1.amazonaws.com/maven-repo,false,null,null,null,null",
    })
    void test_pattern(String url, boolean matches, String domain, String account, String region, String repo) {
        Matcher matcher = CodeArtifactCredsPlugin.CODE_ARTIFACT_MAVEN_PATTERN.matcher(url);
        assertEquals(matches, matcher.matches());
        if (!matches) return;
        assertEquals(domain, matcher.group(1));
        assertEquals(account, matcher.group(2));
        assertEquals(region, matcher.group(3));
        assertEquals(repo, matcher.group(4));
    }

}
