package finalmission.controller.dto;

import finalmission.domain.Gym;
import finalmission.domain.Trainer;
import java.time.LocalTime;
import java.util.List;

public record ReservationSlotsResponse(Long gymId, Long trainerId, List<ReservationSlot> reservations) {

    public static ReservationSlotsResponse from(Gym gym, Trainer trainer, List<LocalTime> times, List<LocalTime> booked) {
        final List<ReservationSlot> slots = times.stream().map(
                time -> {
                    if (booked.contains(time)) {
                        final long count = booked.stream()
                                .filter(t -> t.equals(time))
                                .count();
                        return new ReservationSlot(time, Math.toIntExact(count));
                    }
                    return new ReservationSlot(time, 0);
                }
        ).toList();
        return new ReservationSlotsResponse(gym.getId(), trainer.getId(), slots);
    }

    public record ReservationSlot(LocalTime startAt, int waitCount) {
    }
}
