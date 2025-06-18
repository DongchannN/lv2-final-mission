package finalmission.controller;

import finalmission.controller.dto.ReservationResponse;
import finalmission.controller.dto.ReservationUpdateRequest;
import finalmission.controller.dto.ReservationsPreviewResponse;
import finalmission.global.LoginUser;
import finalmission.service.ReservationService;
import io.swagger.v3.oas.annotations.media.Schema;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class MemberReservationController {

    private final ReservationService reservationService;

    public MemberReservationController(ReservationService reservationService) {
        this.reservationService = reservationService;
    }

    @GetMapping("/api/members/reservations")
    public ResponseEntity<ReservationsPreviewResponse> getMyReservations(@Schema(hidden = true) LoginUser loginUser, Pageable pageable) {
        final ReservationsPreviewResponse myReservations = reservationService.getReservationsByMemberId(loginUser.id(), pageable);
        return ResponseEntity.ok(myReservations);
    }

    @GetMapping("/api/members/reservations/{reservationId}")
    public ResponseEntity<ReservationResponse> getMyReservation(@PathVariable Long reservationId, @Schema(hidden = true) LoginUser loginUser) {
        final ReservationResponse reservation = reservationService.getReservation(loginUser.id(), reservationId);
        return ResponseEntity.ok(reservation);
    }

    @DeleteMapping("/api/members/reservations/{reservationId}")
    public ResponseEntity<Void> cancelReservation(@PathVariable Long reservationId, @Schema(hidden = true) LoginUser loginUser) {
        reservationService.deleteReservation(loginUser.id(), reservationId);
        return ResponseEntity.noContent().build();
    }

    @PatchMapping("/api/members/reservations/{reservationId}")
    public ResponseEntity<Void> updateReservation(@PathVariable Long reservationId,
                                                  @RequestBody ReservationUpdateRequest reservationUpdateRequest,
                                                  @Schema(hidden = true) LoginUser loginUser) {
        reservationService.updateReservation(
                loginUser.id(),
                reservationId,
                reservationUpdateRequest.gymId(),
                reservationUpdateRequest.trainerId(),
                reservationUpdateRequest.date(),
                reservationUpdateRequest.time()
        );
        return ResponseEntity.ok().build();
    }
}
