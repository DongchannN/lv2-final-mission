package finalmission.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

class JwtServiceTest {

    private JwtService jwtService;

    @BeforeEach
    void setUp() {
        jwtService = new JwtService("testtesttesttesttesttesttesttesttesttesttesttesttesttesttesttest");
    }

    @Test
    @DisplayName("Jwt 토큰을 생성한다.")
    void generateJwtTokenTest() {
        // given
        String payload = "hello";

        // when
        final String token = jwtService.generateToken(payload);

        // then
        assertThat(token.split("\\.")).hasSize(3);
    }

    @Test
    @DisplayName("Jwt 토큰으로부터 값을 추출한다")
    void extractPayloadFromToken() {
        // given
        String payload = "hello";
        final String token = jwtService.generateToken(payload);

        // when
        final String extracted = jwtService.resolveToken(token);

        // then
        assertThat(extracted).isEqualTo(payload);
    }
}