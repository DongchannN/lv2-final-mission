package finalmission;

import static org.assertj.core.api.Assertions.assertThat;

import finalmission.domain.Gym;
import finalmission.domain.Member;
import finalmission.domain.Trainer;
import finalmission.repository.GymRepository;
import finalmission.repository.MemberRepository;
import finalmission.repository.TrainerRepository;
import finalmission.service.JwtService;
import io.restassured.RestAssured;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.HttpStatus;

@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
public class SecurityTest {

    @Autowired
    private JwtService jwtService;

    @Autowired
    private MemberRepository memberRepository;

    @Autowired
    private TrainerRepository trainerRepository;

    @Autowired
    private GymRepository gymRepository;

    @LocalServerPort
    int port;

    private Member member;
    private Trainer trainer;

    @BeforeEach
    void setUp() {
        RestAssured.port = port;

        final Gym gym = gymRepository.save(new Gym("gym 1", "location 1", "01044444444"));
        member = memberRepository.save(new Member("member 1", "01055555555", "1234", "1234", 1, gym));
        trainer = trainerRepository.save(new Trainer("trainer 1", "01066666666", "1234", 1, "", "", gym));
    }

    @Test
    @DisplayName("헬스장 회원 권한 테스트")
    void memberAuthTest() {
        // given
        final String token = makeHeaderValue(member.getId(), "ROLE_MEMBER");

        // when
        final int statusCode = RestAssured
                .given().log().all()
                .header("Authorization", token)
                .when()
                .get("/api/member/test")
                .then()
                .log().all()
                .extract()
                .statusCode();

        // then
        assertThat(statusCode).isEqualTo(HttpStatus.OK.value());
    }

    @Test
    @DisplayName("헬스장 회원 권한 실패 테스트")
    void memberAuthFailureTest() {
        // given
        final String token = makeHeaderValue(member.getId(), "ROLE_MEMBER");

        // when
        final int statusCode = RestAssured
                .given().log().all()
                .header("Authorization", token)
                .when()
                .get("/api/trainers/test")
                .then()
                .log().all()
                .extract()
                .statusCode();

        // then
        assertThat(statusCode).isEqualTo(HttpStatus.FORBIDDEN.value());
    }

    @Test
    @DisplayName("헬스장 트레이너 권한 테스트")
    void trainerAuthTest() {
        // given
        final String token = makeHeaderValue(trainer.getId(), "ROLE_TRAINER");

        // when
        final int statusCode = RestAssured
                .given().log().all()
                .header("Authorization", token)
                .when()
                .get("/api/trainers/test")
                .then()
                .log().all()
                .extract()
                .statusCode();

        // then
        assertThat(statusCode).isEqualTo(HttpStatus.OK.value());
    }

    @Test
    @DisplayName("헬스장 트레이너 권한 실패 테스트")
    void trainerFailureAuthTest() {
        // given
        final String token = makeHeaderValue(trainer.getId(), "ROLE_TRAINER");

        // when
        final int statusCode = RestAssured
                .given().log().all()
                .header("Authorization", token)
                .when()
                .get("/api/member/test")
                .then()
                .log().all()
                .extract()
                .statusCode();

        // then
        assertThat(statusCode).isEqualTo(HttpStatus.FORBIDDEN.value());
    }

    private String makeHeaderValue(Long id, String role) {
        return "Bearer " + jwtService.generateToken(id.toString(), role);
    }
}
