package info.ankin.projects.git.httpbackend.model;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

/**
 * These services can be enabled/disabled using the per-repository
 * configuration file:
 */
@RequiredArgsConstructor
@Getter
public enum Service {
    /**
     * This serves Git clients older than version 1.6.6 that are
     * unable to use the upload pack service. When enabled, clients
     * are able to read any file within the repository, including
     * objects that are no longer reachable from a branch but are
     * still present. It is enabled by default, but a repository can
     * disable it by setting this configuration item to false.
     */
    get_any_file("getanyfile"),
    /**
     * This serves git fetch-pack and git ls-remote clients. It is
     * enabled by default, but a repository can disable it by
     * setting this configuration item to false.
     */
    upload_pack("uploadpack"),
    /**
     * This serves git send-pack clients, allowing push. It is
     * disabled by default for anonymous users, and enabled by
     * default for users authenticated by the web server. It can be
     * disabled by setting this item to false, or enabled for all
     * users, including anonymous users, by setting it to true.
     */
    receive_pack("receivepack"),
    ;
    private final String configKey;
}
