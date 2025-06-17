package finalmission.controller.dto;

import finalmission.domain.Reservation;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

public record TrainerLessonsResponse(List<TrainerLesson> lessons) {

    public record TrainerLesson(String memberName, LocalDate date, LocalTime time) {

        public static TrainerLesson from(Reservation reservation) {
            return new TrainerLesson(reservation.getMember().getName(), reservation.getDate(), reservation.getTime());
        }
    }
}
