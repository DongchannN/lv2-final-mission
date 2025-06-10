package finalmission.controller;

import finalmission.controller.dto.ReservationSlotsResponse;
import finalmission.controller.dto.ReserveRequest;
import finalmission.global.LoginMember;
import finalmission.service.ReservationService;
import io.swagger.v3.oas.annotations.media.Schema;
import java.time.LocalDate;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class ReservationController {

    private final ReservationService reservationService;

    public ReservationController(ReservationService reservationService) {
        this.reservationService = reservationService;
    }

    @GetMapping("/api/reservations")
    public ResponseEntity<ReservationSlotsResponse> getReservationWithAvailability(@RequestParam(name = "gymId") Long gymId,
                                                                                   @RequestParam(name = "trainerId") Long trainerId,
                                                                                   @RequestParam(name = "date") LocalDate date,
                                                                                   @Schema(hidden = true) LoginMember loginMember) {
        final ReservationSlotsResponse slots = reservationService.getReservationSlotsByGymAndTrainerAndDate(
                gymId, trainerId, date
        );
        return ResponseEntity.ok(slots);
    }

    @PostMapping("/api/reservations")
    public ResponseEntity<Void> reserve(@RequestBody ReserveRequest reserveRequest, @Schema(hidden = true) LoginMember loginMember) {
        reservationService.addReservation(
                loginMember.id(),
                reserveRequest.gymId(),
                reserveRequest.trainerId(),
                reserveRequest.date() ,
                reserveRequest.time()
        );
        return ResponseEntity.ok().build();
    }
}
