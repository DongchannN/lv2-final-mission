package finalmission.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import finalmission.domain.Gym;
import finalmission.domain.Trainer;
import finalmission.repository.GymRepository;
import finalmission.repository.TrainerRepository;
import org.junit.jupiter.api.BeforeEach;
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
    private Trainer trainer;

    @BeforeEach
    void setUp() {
        gym = gymRepository.save(new Gym("gym", "location", "01012341234"));
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
}
