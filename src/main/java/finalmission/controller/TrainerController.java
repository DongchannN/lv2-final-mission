package finalmission.controller;

import finalmission.controller.dto.CreateTrainerScheduleRequest;
import finalmission.controller.dto.TrainerLessonsResponse;
import finalmission.controller.dto.TrainerResponse;
import finalmission.controller.dto.TrainerSchedulesResponse;
import finalmission.controller.dto.UpdateTrainerInfoRequest;
import finalmission.controller.dto.UpdateTrainerScheduleRequest;
import finalmission.service.ReservationService;
import finalmission.service.TrainerScheduleService;
import finalmission.service.TrainerService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/trainers")
@RequiredArgsConstructor
public class TrainerController {

    private final ReservationService reservationService;
    private final TrainerScheduleService trainerScheduleService;
    private final TrainerService trainerService;

    @GetMapping("/{trainerId}/lessons")
    public TrainerLessonsResponse getTrainerLessons(@PathVariable Long trainerId) {
        return reservationService.getLessonByTrainerId(trainerId);
    }

    @DeleteMapping("/{trainerId}/lessons/{lessonId}")
    public void denyLesson(@PathVariable Long trainerId, @PathVariable Long lessonId) {
        reservationService.denyTrainerLesson(trainerId, lessonId);
    }

    @GetMapping("/{trainerId}/times")
    public TrainerSchedulesResponse getSchedules(@PathVariable Long trainerId) {
        return trainerScheduleService.getTrainerSchedule(trainerId);
    }

    @PostMapping("/{trainerId}/times")
    public void addSchedule(@PathVariable Long trainerId, @RequestBody CreateTrainerScheduleRequest scheduleRequest) {
        trainerScheduleService.addTrainerSchedule(trainerId, scheduleRequest.time());
    }

    @DeleteMapping("/{trainerId}/times/{scheduleId}")
    public void deleteSchedule(@PathVariable Long trainerId, @PathVariable Long scheduleId) {
        trainerScheduleService.deleteTrainerSchedule(trainerId, scheduleId);
    }

    @PutMapping("/{trainerId}/times/{scheduleId}")
    public void updateSchedule(@PathVariable Long trainerId,
                               @PathVariable Long scheduleId,
                               @RequestBody UpdateTrainerScheduleRequest scheduleRequest) {
        trainerScheduleService.updateTrainerSchedule(trainerId, scheduleId, scheduleRequest.time());
    }

    @GetMapping("/{trainerId}/mine")
    public TrainerResponse myPage(@PathVariable Long trainerId) {
        return trainerService.getTrainerInfoById(trainerId);
    }

    @PutMapping("/{trainerId}/mine")
    public void updateMyInfo(@PathVariable Long trainerId, @RequestBody UpdateTrainerInfoRequest updateTrainerInfoRequest) {
        trainerService.updateTrainer(
                trainerId,
                updateTrainerInfoRequest.name(),
                updateTrainerInfoRequest.creditPrice(),
                updateTrainerInfoRequest.description(),
                updateTrainerInfoRequest.imageUrl(),
                updateTrainerInfoRequest.gymId()
        );
    }
}
