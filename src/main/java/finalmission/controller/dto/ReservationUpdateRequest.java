package finalmission.controller.dto;

import java.time.LocalDate;
import java.time.LocalTime;

public record ReservationUpdateRequest(Long gymId, Long trainerId, LocalDate date, LocalTime time) {
}
