package finalmission.controller.dto;

import finalmission.domain.TrainerSchedule;
import java.time.LocalTime;
import java.util.List;

public record TrainerSchedulesResponse(List<TrainerScheduleSlot> schedules) {

    public record TrainerScheduleSlot(LocalTime time) {

        public static TrainerScheduleSlot from(TrainerSchedule schedule) {
            return new TrainerScheduleSlot(schedule.getTime());
        }
    }
}
