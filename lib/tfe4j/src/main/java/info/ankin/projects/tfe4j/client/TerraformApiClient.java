package info.ankin.projects.tfe4j.client;

import info.ankin.projects.tfe4j.client.model.JsonApiErrors;
import info.ankin.projects.tfe4j.client.model.Models;
import info.ankin.projects.tfe4j.client.model.TerraformClientResponseException;
import info.ankin.projects.tfe4j.client.model.Wrappers;
import lombok.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.web.reactive.function.client.ClientRequest;
import org.springframework.web.reactive.function.client.ExchangeFilterFunction;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

public class TerraformApiClient {
    protected final WebClient webClient;

    public TerraformApiClient(WebClient.Builder builder) {
        this(builder
                .baseUrl("https://app.terraform.io/api/v2")
                .filter(ExchangeFilterFunction.ofRequestProcessor(r -> Mono.just(ClientRequest.from(r)
                        // todo use https://docs.spring.io/spring-hateoas/docs/current/reference/html/#mediatypes.community.json:api
                        // but since it ties to a spring version, Models and Wrappers will do
                        .header(HttpHeaders.CONTENT_TYPE, "application/vnd.api+json")
                        .build())))
                .filter(ExchangeFilterFunction.ofResponseProcessor(r -> {
                    if (r.statusCode().is4xxClientError() ||
                        r.statusCode().is5xxServerError()) {
                        return r.bodyToMono(JsonApiErrors.class).map(TerraformClientResponseException::new).flatMap(Mono::error);
                    }
                    return Mono.just(r);
                }))
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

        public Mono<ResponseEntity<Models.SingleUser>> readCurrentEntity() {
            return terraformApiClient.webClient.get().uri("/account/details").retrieve().toEntity(Models.SingleUser.class);
        }

        public Mono<Models.SingleUser> updateCurrent(Models.SingleUserUpdate update) {
            return terraformApiClient.webClient.patch().uri("/account/update").bodyValue(update).retrieve().bodyToMono(Models.SingleUser.class);
        }

        public Mono<ResponseEntity<Models.SingleUser>> updateCurrentEntity(Models.SingleUserUpdate update) {
            return terraformApiClient.webClient.patch().uri("/account/update").bodyValue(update).retrieve().toEntity(Models.SingleUser.class);
        }

        private Mono<Models.SingleUser> updateCurrent(Wrappers.Item<Models.UserUpdate> userUpdateItem) {
            return updateCurrent((Models.SingleUserUpdate) new Models.SingleUserUpdate().setData(userUpdateItem));
        }

        public Mono<Models.SingleUser> updateCurrent(Models.UserUpdate update) {
            return updateCurrent(new Models.UserUpdateItem().setAttributes(update));
        }

        public Mono<ResponseEntity<Models.SingleUser>> updateCurrentEntity(Models.UserUpdate update) {
            return terraformApiClient.webClient.patch().uri("/account/update").bodyValue(new Models.SingleUserUpdate().setData(new Models.UserUpdateItem().setAttributes(update))).retrieve().toEntity(Models.SingleUser.class);
        }

    }
}
