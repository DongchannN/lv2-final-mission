package finalmission.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import finalmission.domain.Gym;
import finalmission.repository.GymRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

@ActiveProfiles("test")
@SpringBootTest
class GymServiceTest {

    @Autowired
    private GymService gymService;

    @Autowired
    private GymRepository gymRepository;

    private Gym gym;

    @BeforeEach
    void setUp() {
        gym = gymRepository.save(new Gym("Gym 1", "Location 1", "01099999999"));
    }

    @Test
    @DisplayName("헬스장을 조회할 수 있다")
    void getGymByIdTest() {
        // when
        final Gym find = gymService.getGymById(gym.getId());

        // then
        assertThat(find.getId()).isEqualTo(gym.getId());
    }

    @Test
    @DisplayName("존재하지 않는 id로 조회 시 예외가 발생한다.")
    void getGymByNonExistIdTest() {
        // when, then
        assertThatThrownBy(() -> gymService.getGymById(999L))
                .isInstanceOf(IllegalArgumentException.class);
    }
}