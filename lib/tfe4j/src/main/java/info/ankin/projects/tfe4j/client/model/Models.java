package info.ankin.projects.tfe4j.client.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.experimental.Accessors;

public interface Models {
    class SingleUser extends Wrappers.Single<User> {
    }

    class UserItem extends Wrappers.Item<User> {
        public UserItem() {
            setType("users");
        }
    }

    class SingleUserUpdate extends Wrappers.Single<UserUpdate> {
    }

    class UserUpdateItem extends Wrappers.Item<UserUpdate> {
        public UserUpdateItem() {
            setType("users");
        }
    }

    @Data
    @Accessors(chain = true)
    class User {
        @JsonProperty("avatar-url")
        String avatarURL;
        String email;
        @JsonProperty("is-service-account")
        Boolean isServiceAccount;
        @JsonProperty("two-factor")
        TwoFactor twoFactor;
        @JsonProperty("unconfirmed-email")
        String unconfirmedEmail;
        String username;
        /**
         * not documented but present in cloud api
         */
        String password;
        @JsonProperty("v2-only")
        Boolean v2Only;
        @JsonProperty("is-site-admin")
        Boolean isSiteAdmin;
        @JsonProperty("is-sso-login")
        Boolean isSsoLogin;
        UserPermissions permissions;

        @JsonProperty("enterprise-support")
        Boolean enterpriseSupport;
        @JsonProperty("has-git-hub-app-token")
        Boolean hasGitHubAppToken;
        @JsonProperty("is-confirmed")
        Boolean confirmed;
        @JsonProperty("is-sudo")
        Boolean sudo;
        @JsonProperty("has-linked-hcp")
        Boolean hasLinkedHcp;

        @Data
        @Accessors(chain = true)
        public static class TwoFactor {
            Boolean enabled;
            Boolean verified;
        }

        @Data
        @Accessors(chain = true)
        public static class UserPermissions {
            @JsonProperty("can-create-organizations")
            Boolean createOrganizations;
            @JsonProperty("can-change-email")
            Boolean changeEmail;
            @JsonProperty("can-change-username")
            Boolean changeUsername;
            @JsonProperty("can-manage-user-tokens")
            Boolean manageUserTokens;
            @JsonProperty("can-view2fa-settings")
            Boolean view2FaSettings;
            @JsonProperty("can-manage-hcp-account")
            Boolean manageHcpAccount;
            // undocumented
            @JsonProperty("can-view-settings")
            Boolean viewSettings;
        }
    }

    @Data
    @Accessors(chain = true)
    class UserUpdate {
        /**
         * email can't be blank
         */
        String email;

        /**
         * username can't be blank
         */
        String username;
    }

}
