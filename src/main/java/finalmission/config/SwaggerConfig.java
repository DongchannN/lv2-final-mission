package finalmission.config;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;

@Configuration
@EnableWebMvc
public class SwaggerConfig {

    @Bean
    public OpenAPI openAPI() {
        return new OpenAPI()
                .components(new Components())
                .info(apiInfo());
    }

    private Info apiInfo() {
        return new Info()
                .title("헬스장 예약 서비스")
                .description("모든 요청은 회원 가입한 회원 대상으로 가능합니다. \n로그인 이후 발급 받은 토큰을 Authrization Bearer 헤더에 삽입해주세요.")
                .version("1.0.0");
    }
}
