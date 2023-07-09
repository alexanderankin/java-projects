package info.ankin.projects.tfe4j.client;

import info.ankin.projects.tfe4j.client.model.Models;
import lombok.SneakyThrows;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class TerraformEnterpriseClientTest extends BaseTest {

    @SneakyThrows
    @Test
    void test_adminOps_listOrg_parsing() {
        var orgs = OBJECT_MAPPER.readValue(read("/info/ankin/projects/tfe4j/client/model/test_adminOps_listOrg_parsing.response1.json"),
                Models.MultipleOrganizations.class);
        System.out.println(orgs);
    }

}
