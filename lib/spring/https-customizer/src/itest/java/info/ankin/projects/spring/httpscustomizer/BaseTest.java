package info.ankin.projects.spring.httpscustomizer;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.util.DefaultUriBuilderFactory;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class BaseTest {
    @LocalServerPort
    Integer port;

    protected RestTemplate restTemplate;

    @Autowired
    public void setRestTemplate(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
        this.restTemplate.setUriTemplateHandler(new DefaultUriBuilderFactory(getBaseUrl()));
    }

    protected WebClient webClient;

    @Autowired
    public void setWebClient(WebClient webClient) {
        this.webClient = webClient.mutate().baseUrl(getBaseUrl()).build();
    }

    private String getBaseUrl() {
        return "https://localhost:" + port;
    }
}
