package finalmission.controller.dto;

import finalmission.domain.Reservation;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

public record ReservationsPreviewResponse(List<ReservationPreviewSlot> reservations) {

    public static ReservationsPreviewResponse from(List<Reservation> reservations) {
        return new ReservationsPreviewResponse(
                reservations.stream()
                        .map(ReservationPreviewSlot::from)
                        .toList()
        );
    }

    record ReservationPreviewSlot(Long reservationId, LocalDate date, LocalTime time) {

        public static ReservationPreviewSlot from(Reservation reservation) {
            return new ReservationPreviewSlot(
                    reservation.getId(),
                    reservation.getDate(),
                    reservation.getTime()
            );
        }
    }
}
