package finalmission.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestClient;

@Configuration
public class RestClientConfig {

    @Bean
    public RestClient randomNicknameClient() {
        return RestClient.builder().baseUrl("https://randommer.io/api").build();
    }
}
