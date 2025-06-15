package finalmission.repository;

import finalmission.domain.Gym;
import finalmission.domain.Member;
import finalmission.domain.Reservation;
import finalmission.domain.Trainer;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ReservationRepository extends JpaRepository<Reservation, Long> {
    List<Reservation> findReservationsByMember(Member member);

    List<Reservation> findReservationsByGymAndTrainerAndDate(Gym gym, Trainer trainer, LocalDate date);

    boolean existsByGymAndTrainerAndDateAndTime(Gym gym, Trainer trainer, LocalDate date, LocalTime time);

    boolean existsByGymAndTrainerAndDateAndTimeAndMemberNot(Gym gym, Trainer trainer, LocalDate date, LocalTime time, Member member);

    List<Reservation> findReservationsByGymAndTrainerAndDateAndTime(Gym gym, Trainer trainer, LocalDate date, LocalTime time);

    Optional<Reservation> findFirstByGymAndTrainerAndDateAndTimeOrderById(Gym gym, Trainer trainer, LocalDate date, LocalTime time);
}
