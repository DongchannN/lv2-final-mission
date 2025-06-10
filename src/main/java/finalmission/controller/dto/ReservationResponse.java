package finalmission.controller.dto;

import finalmission.domain.Reservation;
import java.time.LocalDate;
import java.time.LocalTime;

public record ReservationResponse(String gymName, String trainerName, LocalDate date, LocalTime time) {

    public static ReservationResponse from(Reservation reservation) {
        return new ReservationResponse(
                reservation.getGym().getName(),
                reservation.getTrainer().getName(),
                reservation.getDate(),
                reservation.getTime()
        );
    }
}
