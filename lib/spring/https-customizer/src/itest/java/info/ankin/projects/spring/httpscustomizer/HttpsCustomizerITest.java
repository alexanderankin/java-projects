package info.ankin.projects.spring.httpscustomizer;

import org.junit.jupiter.api.Test;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.reactive.function.client.ClientResponse;
import org.springframework.web.util.DefaultUriBuilderFactory;
import reactor.core.publisher.Mono;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;
import static org.junit.jupiter.api.Assertions.assertThrows;

class HttpsCustomizerITest extends BaseTest {

    @Test
    void contextLoads() {
    }

    @Test
    void httpsRequestsWorkWithWebClient() {
        ResponseEntity<String> entity = webClient.get().uri("/").exchangeToMono(this::parseResponse).block();
        assertThat(entity, is(notNullValue()));
        assertThat(entity.getStatusCode(), is(HttpStatus.NOT_FOUND));
    }

    private Mono<ResponseEntity<String>> parseResponse(ClientResponse c) {
        return c.toEntity(String.class);
    }

    @Test
    void httpsRequestsWorkWithRestTemplate() {
        HttpClientErrorException exception = assertThrows(HttpClientErrorException.class,
                () -> restTemplate.getForEntity("/", String.class));
        assertThat(exception.getStatusCode(), is(HttpStatus.NOT_FOUND));
    }

    @Test
    void httpFails() {
        RestTemplate rt = restTemplate("http://localhost:");
        assertThrows(ResourceAccessException.class, () -> rt.getForEntity("/", String.class));
    }

    @Test
    void httpsWithVerificationFails() {
        RestTemplate rt = restTemplate("https://localhost:");
        assertThrows(ResourceAccessException.class, () -> rt.getForEntity("/", String.class));
    }

    private RestTemplate restTemplate(String baseUrl) {
        return new RestTemplateBuilder().uriTemplateHandler(new DefaultUriBuilderFactory(baseUrl + port)).build();
    }

}
