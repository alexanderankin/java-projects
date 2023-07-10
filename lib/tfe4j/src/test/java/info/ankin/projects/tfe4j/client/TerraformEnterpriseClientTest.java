package info.ankin.projects.tfe4j.client;

import info.ankin.projects.tfe4j.client.model.Models;
import info.ankin.projects.tfe4j.client.model.TerraformClientResponseException;
import lombok.SneakyThrows;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.springframework.web.reactive.function.client.WebClient;

class TerraformEnterpriseClientTest extends BaseTest {

    TerraformEnterpriseClient client = new TerraformEnterpriseClient(WebClient.builder()
            .defaultHeaders(h -> h.setBearerAuth(USER_TOKEN))
            .baseUrl("http://localhost:8080")
            .build());

    @SneakyThrows
    @Test
    void test_adminOps_listOrg_parsing() {
        var orgs = OBJECT_MAPPER.readValue(read("/info/ankin/projects/tfe4j/client/model/test_adminOps_listOrg_parsing.response1.json"),
                Models.MultipleOrganizations.class);
        System.out.println(orgs);
    }

    @Disabled
    @SneakyThrows
    @Test
    void test_adminOps_listOrg() {
        try {
            Models.MultipleOrganizations block = client.adminOps().listOrganizations(new Models.ListOrganizationsParameters().setName("abc")).block();
            System.out.println(block);
        } catch (TerraformClientResponseException e) {
            System.out.println(e.getErrors());
        }
    }

}
