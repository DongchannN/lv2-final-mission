package finalmission.controller;

import finalmission.controller.dto.TrainerLessonsResponse;
import finalmission.service.ReservationService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/trainers")
@RequiredArgsConstructor
public class TrainerController {

    private final ReservationService reservationService;

    @GetMapping("/{trainerId}/lessons")
    public TrainerLessonsResponse getTrainerLessons(@PathVariable Long trainerId) {
        return reservationService.getLessonByTrainerId(trainerId);
    }

    @DeleteMapping("/{trainerId}/lessons/{lessonId}")
    public void denyLesson(@PathVariable Long trainerId, @PathVariable Long lessonId) {
        reservationService.denyTrainerLesson(trainerId, lessonId);
    }
}
