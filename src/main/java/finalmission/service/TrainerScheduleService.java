package finalmission.service;

import finalmission.controller.dto.TrainerSchedulesResponse;
import finalmission.controller.dto.TrainerSchedulesResponse.TrainerScheduleSlot;
import finalmission.domain.Trainer;
import finalmission.domain.TrainerSchedule;
import finalmission.repository.TrainerScheduleRepository;
import jakarta.transaction.Transactional;
import java.time.LocalTime;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class TrainerScheduleService {

    private final TrainerScheduleRepository trainerScheduleRepository;
    private final TrainerService trainerService;

    @Transactional
    public Long addTrainerSchedule(Long trainerId, LocalTime schedule) {
        final Trainer trainer = trainerService.getTrainerById(trainerId);
        return trainerScheduleRepository.save(new TrainerSchedule(trainer, schedule)).getId();
    }

    public TrainerSchedulesResponse getTrainerSchedule(Long trainerId) {
        final Trainer trainer = trainerService.getTrainerById(trainerId);
        final List<TrainerScheduleSlot> schedules = trainerScheduleRepository.findTrainerSchedulesByTrainer(trainer).stream()
                .map(TrainerScheduleSlot::from).toList();
        return new TrainerSchedulesResponse(schedules);
    }

    @Transactional
    public void deleteTrainerSchedule(Long trainerId, Long scheduleId) {
        final TrainerSchedule validTrainerSchedule = getValidTrainerSchedule(trainerId, scheduleId);
        trainerScheduleRepository.deleteById(validTrainerSchedule.getId());
    }

    @Transactional
    public void updateTrainerSchedule(Long trainerId, Long scheduleId, LocalTime time) {
        final TrainerSchedule trainerSchedule = getValidTrainerSchedule(trainerId, scheduleId);
        trainerSchedule.updateTime(time);
    }

    private TrainerSchedule getValidTrainerSchedule(Long trainerId, Long scheduleId) {
        final Trainer trainer = trainerService.getTrainerById(trainerId);
        final TrainerSchedule trainerSchedule = trainerScheduleRepository.findById(scheduleId)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 시간입니다."));
        if (!trainer.getId().equals(trainerSchedule.getTrainer().getId())) {
            throw new IllegalArgumentException("본인의 일정이 아닙니다.");
        }
        return trainerSchedule;
    }
}
