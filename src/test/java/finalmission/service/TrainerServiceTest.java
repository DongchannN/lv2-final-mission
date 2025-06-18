package finalmission.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertAll;

import finalmission.domain.Gym;
import finalmission.domain.Trainer;
import finalmission.repository.GymRepository;
import finalmission.repository.TrainerRepository;
import jakarta.transaction.Transactional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

@Transactional
@ActiveProfiles("test")
@SpringBootTest
class TrainerServiceTest {

    @Autowired
    private TrainerService trainerService;

    @Autowired
    private TrainerRepository trainerRepository;
    @Autowired
    private GymRepository gymRepository;

    private Gym gym;
    private Gym gym2;
    private Trainer trainer;

    @BeforeEach
    void setUp() {
        gym = gymRepository.save(new Gym("gym", "location", "01012341234"));
        gym2 = gymRepository.save(new Gym("gym 2", "location 2", "01099999999"));
        trainer = trainerRepository.save(new Trainer("name", "01054325423", "1234", 10, "hello", "image", gym));
    }

    @Test
    void getTrainerByIdTest() {
        // when
        final Trainer byId = trainerService.getTrainerById(trainer.getId());

        // then
        assertThat(byId.getId()).isEqualTo(trainer.getId());
    }

    @Test
    void getTrainerByNonExistIdTest() {
        // when, then
        assertThatThrownBy(() -> trainerService.getTrainerById(999L))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    @DisplayName("트레이너 정보 수정 테스트")
    void updateTrainerTest() {
        // given
        String name = "updated name";
        String description = "updated description";
        String imageUrl = "updated imageUrl";
        int creditPrice = 200;
        Long gymId = gym2.getId();

        // when
        trainerService.updateTrainer(trainer.getId(), name, 200, description, imageUrl, gymId);

        // then
        final Trainer updated = trainerRepository.findById(trainer.getId()).orElseThrow();
        assertAll(
                () -> assertThat(updated.getName()).isEqualTo(name),
                () -> assertThat(updated.getDescription()).isEqualTo(description),
                () -> assertThat(updated.getImageUrl()).isEqualTo(imageUrl),
                () -> assertThat(updated.getCreditPrice()).isEqualTo(creditPrice),
                () -> assertThat(updated.getGym().getId()).isEqualTo(gymId)
        );
    }

    @Test
    @DisplayName("트레이너 로그인 테스트")
    void trainerLoginTest() {
        // given
        final String phoneNumber = trainer.getPhoneNumber();
        final String password = trainer.getPassword();

        // when
        final Long trainerId = trainerService.authenticate(phoneNumber, password);

        // then
        assertThat(trainerId).isEqualTo(trainer.getId());
    }

    @Test
    @DisplayName("비밀번호 불일치 시 로그인 실패")
    void trainerLoginFailureTest() {
        // given
        final String phoneNumber = trainer.getPhoneNumber();
        final String password = "12345";

        // when, then
        assertThatThrownBy(() -> trainerService.authenticate(phoneNumber, password))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    @DisplayName("존재하지 않는 트레이너일 시 로그인 실패")
    void nonExistTrainerLoginTest() {
        // given
        final String phoneNumber = "01000000000";
        final String password = "1234";

        // when, then
        assertThatThrownBy(() -> trainerService.authenticate(phoneNumber, password))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    @DisplayName("트레이너 등록 테스트")
    void addTrainerTest() {
        // given
        String name = "name";
        String phoneNumber = "01043214321";
        String description = "description";
        String imageUrl = "url";
        String password = "1234";
        int creditPrice = 100;
        long gymId = gym.getId();

        // when
        final Long id = trainerService.addTrainer(name, phoneNumber, password, creditPrice, description, imageUrl,
                gymId);

        // then
        final Trainer saved = trainerRepository.findById(id).orElseThrow();
        assertAll(
                () -> assertThat(saved.getName()).isEqualTo(name),
                () -> assertThat(saved.getDescription()).isEqualTo(description),
                () -> assertThat(saved.getImageUrl()).isEqualTo(imageUrl),
                () -> assertThat(saved.getCreditPrice()).isEqualTo(creditPrice),
                () -> assertThat(saved.getPassword()).isEqualTo(password),
                () -> assertThat(saved.getGym().getId()).isEqualTo(gymId)
        );
    }
}
