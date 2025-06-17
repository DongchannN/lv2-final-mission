package finalmission.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertAll;

import finalmission.domain.Gym;
import finalmission.domain.Trainer;
import finalmission.repository.GymRepository;
import finalmission.repository.TrainerRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

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
        trainer = trainerRepository.save(new Trainer("name", 10, "hello", "image", gym));
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
}
