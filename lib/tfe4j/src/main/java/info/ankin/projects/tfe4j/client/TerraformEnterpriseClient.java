package info.ankin.projects.tfe4j.client;

import lombok.Value;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

public class TerraformEnterpriseClient extends TerraformApiClient {
    public TerraformEnterpriseClient(WebClient.Builder builder) {
        super(builder);
    }

    public TerraformEnterpriseClient(WebClient webClient) {
        super(webClient);
    }

    public AdminOps adminOps() {
        return new AdminOps(this);
    }

    @Value
    public static class AdminOps {
        TerraformEnterpriseClient terraformEnterpriseClient;

        public Mono<String> listOrganizations() {
            return terraformEnterpriseClient.webClient.get().uri("/admin/organizations").retrieve().bodyToMono(String.class);
        }

        /*
u := "admin/organizations"
	req, err := s.client.NewRequest("GET"
u := fmt.Sprintf("admin/organizations/%s/relationships/module-consumers", url.QueryEscape(organization))

	req, err := s.client.NewRequest("GET"
u := fmt.Sprintf("admin/organizations/%s", url.QueryEscape(organization))
	req, err := s.client.NewRequest("GET"
u := fmt.Sprintf("admin/organizations/%s", url.QueryEscape(organization))
	req, err := s.client.NewRequest("PATCH"
u := fmt.Sprintf("admin/organizations/%s/relationships/module-consumers", url.QueryEscape(organization))

	var organizations []*AdminOrganizationID
	for _, id := range consumerOrganizationIDs {
		if !validStringID(&id) {
			return ErrInvalidOrg
		}
		organizations = append(organizations, &AdminOrganizationID{ID: id})
	}

	req, err := s.client.NewRequest("PATCH"
u := fmt.Sprintf("admin/organizations/%s", url.QueryEscape(organization))
	req, err := s.client.NewRequest("DELETE"
         */
    }
}
