package finalmission.controller;

import static finalmission.controller.MemberController.JWT_PREFIX;

import finalmission.controller.dto.CreateTrainerScheduleRequest;
import finalmission.controller.dto.SigninRequest;
import finalmission.controller.dto.TrainerLessonsResponse;
import finalmission.controller.dto.TrainerResponse;
import finalmission.controller.dto.TrainerSchedulesResponse;
import finalmission.controller.dto.TrainerSignupRequest;
import finalmission.controller.dto.UpdateTrainerInfoRequest;
import finalmission.controller.dto.UpdateTrainerScheduleRequest;
import finalmission.service.JwtService;
import finalmission.service.ReservationService;
import finalmission.service.TrainerScheduleService;
import finalmission.service.TrainerService;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
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
    private final JwtService jwtService;

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
    public void updateMyInfo(@PathVariable Long trainerId,
                             @RequestBody UpdateTrainerInfoRequest updateTrainerInfoRequest) {
        trainerService.updateTrainer(
                trainerId,
                updateTrainerInfoRequest.name(),
                updateTrainerInfoRequest.creditPrice(),
                updateTrainerInfoRequest.description(),
                updateTrainerInfoRequest.imageUrl(),
                updateTrainerInfoRequest.gymId()
        );
    }

    @PostMapping("/signin")
    public ResponseEntity<Void> login(@RequestBody SigninRequest signinRequest, HttpServletResponse response) {
        final Long trainerId = trainerService.authenticate(signinRequest.phoneNumber(), signinRequest.password());
        final String token = jwtService.generateToken(trainerId.toString(), "ROLE_TRAINER");
        response.setHeader("Authentication", JWT_PREFIX + token);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/register")
    public void register(@RequestBody TrainerSignupRequest trainerSignupRequest) {
        trainerService.addTrainer(
                trainerSignupRequest.name(),
                trainerSignupRequest.phoneNumber(),
                trainerSignupRequest.password(),
                trainerSignupRequest.creditPrice(),
                trainerSignupRequest.description(),
                trainerSignupRequest.imageUrl(),
                trainerSignupRequest.gymId()
        );
    }
}
