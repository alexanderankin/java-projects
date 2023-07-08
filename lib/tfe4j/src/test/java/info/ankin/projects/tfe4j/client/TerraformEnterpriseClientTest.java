package info.ankin.projects.tfe4j.client;

import info.ankin.projects.tfe4j.client.model.Models;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.springframework.web.reactive.function.client.WebClient;

@Slf4j
class TerraformEnterpriseClientTest extends BaseTest {

    TerraformEnterpriseClient terraformEnterpriseClient = new TerraformEnterpriseClient(WebClient.builder().defaultHeaders(h -> h.setBearerAuth(ORG_TOKEN)));

    @Disabled
    @SneakyThrows
    @Test
    void test_accountOps_readCurrent() {
        log.debug("{}", terraformEnterpriseClient.accountOps().readCurrent().block());
    }

    @SneakyThrows
    @Test
    void test_accountOps_readCurrent_parsing() {
        var obj = OBJECT_MAPPER.readValue(
                "{\"data\": {\"id\": \"user-\", \"type\": \"users\", \"attributes\":\n" +
                "{\"username\": \"api-org-some-org-token\", \"is-service-account\":\n" +
                "true, \"avatar-url\": \"https://www.gravatar.com/avatar/4a7951a7438df66eb3b30d414cdd359e\",\n" +
                "\"password\": null, \"enterprise-support\": false, \"is-site-admin\":\n" +
                "false, \"is-sso-login\": false, \"two-factor\": {\"enabled\": true, \"verified\":\n" +
                "true}, \"email\": \"api-org-some-org-token@hashicorp.com\", \"unconfirmed-email\":\n" +
                "null, \"has-git-hub-app-token\": false, \"is-confirmed\": true, \"is-sudo\":\n" +
                "false, \"has-linked-hcp\": false, \"permissions\":\n" +
                "{\"can-create-organizations\": false, \"can-view-settings\":\n" +
                "false, \"can-change-email\": true, \"can-change-username\":\n" +
                "true, \"can-manage-user-tokens\": false, \"can-view2fa-settings\":\n" +
                "false, \"can-manage-hcp-account\": false}}, \"relationships\":\n" +
                "{\"authentication-tokens\": {\"links\":\n" +
                "{\"related\": \"/api/v2/users/user-some-user/authentication-tokens\"}}, \"github-app-oauth-tokens\":\n" +
                "{\"links\":\n" +
                "{\"related\": \"/api/v2/users/user-some-user/github-app-oauth-tokens\"}}}, \"links\":\n" +
                "{\"self\": \"/api/v2/users/user-some-user\"}}}\n",
                Models.SingleUser.class);

        log.debug("{}", obj);
    }

}
