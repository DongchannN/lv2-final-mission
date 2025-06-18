package finalmission.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import finalmission.controller.dto.TrainerSchedulesResponse;
import finalmission.controller.dto.TrainerSchedulesResponse.TrainerScheduleSlot;
import finalmission.domain.Gym;
import finalmission.domain.Trainer;
import finalmission.domain.TrainerSchedule;
import finalmission.repository.GymRepository;
import finalmission.repository.TrainerRepository;
import finalmission.repository.TrainerScheduleRepository;
import java.time.LocalTime;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

@ActiveProfiles("test")
@SpringBootTest
class TrainerScheduleServiceTest {

    @Autowired
    private TrainerScheduleService trainerScheduleService;

    @Autowired
    private TrainerRepository trainerRepository;

    @Autowired
    private GymRepository gymRepository;

    @Autowired
    private TrainerScheduleRepository trainerScheduleRepository;

    private LocalTime time;
    private Gym gym;
    private Trainer trainer1;
    private Trainer trainer2;
    private TrainerSchedule trainerSchedule;

    @BeforeEach
    void setUp() {
        gym = gymRepository.save(new Gym("gym 1", "location 1", "01099999999"));
        trainer1 = trainerRepository.save(new Trainer("trainer 1", "01011111111","1234", 100, "description 1", "image 1", gym));
        trainer2 = trainerRepository.save(new Trainer("trainer 2", "01011111112","1234", 200, "description 2", "image 2", gym));
        final LocalTime now = LocalTime.now();
        time = LocalTime.of(now.getHour(), now.getMinute());
        trainerSchedule = trainerScheduleRepository.save(new TrainerSchedule(trainer1, time));
    }

    @Test
    @DisplayName("트레이너는 자신의 일정을 추가할 수 있다.")
    void addTrainerScheduleTest() {
        // given
        final Long trainerId = trainer1.getId();

        // when
        final LocalTime plus1Hour = LocalTime.now().plusHours(1);
        final LocalTime time = LocalTime.of(plus1Hour.getHour(), plus1Hour.getMinute());
        trainerScheduleService.addTrainerSchedule(trainerId, time);

        // then
        final TrainerSchedulesResponse trainerSchedules = trainerScheduleService.getTrainerSchedule(trainerId);
        assertThat(trainerSchedules.schedules()).hasSize(2);
        assertThat(trainerSchedules.schedules()).contains(new TrainerScheduleSlot(time));
    }

    @Test
    @DisplayName("트레이너는 자신의 일정을 삭제할 수 있다.")
    void deleteTrainerScheduleTest() {
        // given
        Long trainerId = trainer1.getId();
        Long scheduleId = trainerSchedule.getId();

        // when
        trainerScheduleService.deleteTrainerSchedule(trainerId, scheduleId);

        // then
        final TrainerSchedulesResponse trainerSchedules = trainerScheduleService.getTrainerSchedule(trainerId);
        assertThat(trainerSchedules.schedules()).hasSize(0);
    }

    @Test
    @DisplayName("트레이너가 자신의 일정이 아닌 일정을 삭제하려고 할 떄 예외가 발생한다.")
    void deleteOtherTrainerScheduleTest() {
        // given
        Long trainerId = trainer2.getId();
        Long scheduleId = trainerSchedule.getId();

        // when, then
        assertThatThrownBy(() -> trainerScheduleService.deleteTrainerSchedule(trainerId, scheduleId))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    @DisplayName("트레이너는 자신의 일정을 수정할 수 있다.")
    void updateScheduleTest() {
        // given
        final LocalTime plus1Hour = LocalTime.now().plusHours(1);
        Long trainerId = trainer1.getId();
        Long scheduleId = trainerSchedule.getId();
        LocalTime toUpdate = LocalTime.of(plus1Hour.getHour(), plus1Hour.getMinute());

        // when
        trainerScheduleService.updateTrainerSchedule(trainerId, scheduleId, toUpdate);

        // then
        final TrainerSchedulesResponse trainerSchedules = trainerScheduleService.getTrainerSchedule(trainerId);
        assertThat(trainerSchedules.schedules()).hasSize(1);
        assertThat(trainerSchedules.schedules()).contains(new TrainerScheduleSlot(toUpdate));
    }

    @Test
    @DisplayName("트레이너는 다른 사람의 일정을 수정할 수 없다.")
    void updateOtherTrainerScheduleTest() {
        // given
        final LocalTime plus1Hour = LocalTime.now().plusHours(1);
        Long trainerId = trainer2.getId();
        Long scheduleId = trainerSchedule.getId();
        LocalTime toUpdate = LocalTime.of(plus1Hour.getHour(), plus1Hour.getMinute());

        // when, then
        assertThatThrownBy(() -> trainerScheduleService.updateTrainerSchedule(trainerId, scheduleId, toUpdate))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    @DisplayName("트레이너는 자신의 일정을 리스트로 받아 확인할 수 있다.")
    void getTrainerSchedulesTest() {
        // given
        Long trainer1Id = trainer1.getId();
        Long trainer2Id = trainer2.getId();

        // when
        final TrainerSchedulesResponse trainerSchedules1 = trainerScheduleService.getTrainerSchedule(trainer1Id);
        final TrainerSchedulesResponse trainerSchedules2 = trainerScheduleService.getTrainerSchedule(trainer2Id);

        // then
        assertThat(trainerSchedules1.schedules()).hasSize(1);
        assertThat(trainerSchedules2.schedules()).hasSize(0);
        assertThat(trainerSchedules1.schedules()).contains(new TrainerScheduleSlot(time));
    }
}
