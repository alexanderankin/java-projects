package info.ankin.projects.tfe4j.client;

import info.ankin.projects.tfe4j.client.model.Models;
import lombok.Value;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

public class TerraformApiClient {
    protected final WebClient webClient;

    public TerraformApiClient(WebClient.Builder builder) {
        this(builder
                .baseUrl("https://app.terraform.io/api/v2")
                .build());
    }

    public TerraformApiClient(WebClient webClient) {
        this.webClient = webClient;
    }

    public AccountOps accountOps() {
        return new AccountOps(this);
    }

    @Value
    public static class AccountOps {
        TerraformApiClient terraformApiClient;

        public Mono<Models.SingleUser> readCurrent() {
            return terraformApiClient.webClient.get().uri("/account/details").retrieve().bodyToMono(Models.SingleUser.class);
        }
    }
}
