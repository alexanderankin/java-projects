package info.ankin.projects.tfe4j.client.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.experimental.Accessors;

public interface Models {
    //<editor-fold desc="user">
    class SingleUser extends Wrappers.Single<User> {
    }

    class UserItem extends Wrappers.Item<User> {
        public UserItem() {
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
    //</editor-fold>

    //<editor-fold desc="user update">
    class SingleUserUpdate extends Wrappers.Single<UserUpdate> {
    }

    class UserUpdateItem extends Wrappers.Item<UserUpdate> {
        public UserUpdateItem() {
            setType("users");
        }

        @Override
        public SingleUserUpdate toSingle() {
            return (SingleUserUpdate) new SingleUserUpdate().setData(this);
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

        public UserUpdateItem toItem() {
            return (UserUpdateItem) new UserUpdateItem().setAttributes(this);
        }
    }
    //</editor-fold>

    //<editor-fold desc="user password update">
    class SingleUserPasswordUpdate extends Wrappers.Single<UserPasswordUpdate> {
    }

    class UserPasswordUpdateItem extends Wrappers.Item<UserPasswordUpdate> {
        public UserPasswordUpdateItem() {
            setType("users");
        }

        @Override
        public SingleUserPasswordUpdate toSingle() {
            return (SingleUserPasswordUpdate) new SingleUserPasswordUpdate().setData(this);
        }
    }

    @Data
    @Accessors(chain = true)
    class UserPasswordUpdate {
        @JsonProperty("current_password")
        String currentPassword;
        String password;
        @JsonProperty("password_confirmation")
        String passwordConfirmation;

        public UserPasswordUpdateItem toItem() {
            return (UserPasswordUpdateItem) new UserPasswordUpdateItem().setAttributes(this);
        }
    }
    //</editor-fold>

    //<editor-fold desc="ent/admin/org">
    class MultipleOrganizations extends Wrappers.Multiple<Organization> {
    }

    @Data
    @Accessors(chain = true)
    class Organization {
        String name;
        @JsonProperty("access-beta-tools")
        Boolean accessBetaTools;
        @JsonProperty("external-id")
        String externalID;
        @JsonProperty("global-module-sharing")
        Boolean globalModuleSharing;
        @JsonProperty("is-disabled")
        Boolean isDisabled;
        @JsonProperty("notification-email")
        String notificationEmail;
        @JsonProperty("sso-enabled")
        Boolean ssoEnabled;
        @JsonProperty("terraform-build-worker-apply-timeout")
        String terraformBuildWorkerApplyTimeout;
        @JsonProperty("terraform-build-worker-plan-timeout")
        String terraformBuildWorkerPlanTimeout;
        @JsonProperty("terraform-worker-sudo-enabled")
        Boolean terraformWorkerSudoEnabled;
        @JsonProperty("workspace-limit")
        Integer workspaceLimit;
        // not implemented in go-tfe
        @JsonProperty("global-provider-sharing")
        Boolean globalProviderSharing;
    }
    //</editor-fold>

}
