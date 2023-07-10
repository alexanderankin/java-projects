package info.ankin.projects.tfe4j.client;

import info.ankin.projects.tfe4j.client.model.Models;
import lombok.Value;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.util.UriBuilder;
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

    // untested - not public on tf cloud
    @Value
    public static class AdminOps {
        TerraformEnterpriseClient client;

        public Mono<Models.MultipleOrganizations> listOrganizations() {
            return listOrganizations(null);
        }

        public Mono<Models.MultipleOrganizations> listOrganizations(Models.ListOrganizationsParameters parameters) {
            return client.webClient.get().uri(u -> {
                UriBuilder builder = u.path("/admin/organizations");
                if (parameters != null) builder.queryParams(client.queryString(parameters));
                return builder.build();
            }).retrieve().bodyToMono(Models.MultipleOrganizations.class);
        }

        public Mono<Models.SingleOrganization> getOrganization(String name) {
            return client.webClient.get().uri("/admin/organizations/{name}", name).retrieve().bodyToMono(Models.SingleOrganization.class);
        }

        public Mono<Models.SingleOrganization> patchOrganization(String name, Models.SingleOrganization singleOrganization) {
            return client.webClient.patch().uri("/admin/organizations/{name}", name)
                    .bodyValue(singleOrganization)
                    .retrieve().bodyToMono(Models.SingleOrganization.class);
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
