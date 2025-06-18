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
import finalmission.global.LoginUser;
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

    @GetMapping("/lessons")
    public TrainerLessonsResponse getTrainerLessons(LoginUser loginUser) {
        return reservationService.getLessonByTrainerId(loginUser.id());
    }

    @DeleteMapping("/lessons/{lessonId}")
    public void denyLesson(LoginUser loginUser, @PathVariable Long lessonId) {
        reservationService.denyTrainerLesson(loginUser.id(), lessonId);
    }

    @GetMapping("/times")
    public TrainerSchedulesResponse getSchedules(LoginUser loginUser) {
        return trainerScheduleService.getTrainerSchedule(loginUser.id());
    }

    @PostMapping("/times")
    public void addSchedule(LoginUser loginUser, @RequestBody CreateTrainerScheduleRequest scheduleRequest) {
        trainerScheduleService.addTrainerSchedule(loginUser.id(), scheduleRequest.time());
    }

    @DeleteMapping("/times/{scheduleId}")
    public void deleteSchedule(LoginUser loginUser, @PathVariable Long scheduleId) {
        trainerScheduleService.deleteTrainerSchedule(loginUser.id(), scheduleId);
    }

    @PutMapping("/times/{scheduleId}")
    public void updateSchedule(LoginUser loginUser,
                               @PathVariable Long scheduleId,
                               @RequestBody UpdateTrainerScheduleRequest scheduleRequest) {
        trainerScheduleService.updateTrainerSchedule(loginUser.id(), scheduleId, scheduleRequest.time());
    }

    @GetMapping("/mine")
    public TrainerResponse myPage(LoginUser loginUser) {
        return trainerService.getTrainerInfoById(loginUser.id());
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

    @GetMapping("/test")
    public ResponseEntity<String> test(LoginUser loginUser) {
        return ResponseEntity.ok(loginUser.id().toString());
    }
}
