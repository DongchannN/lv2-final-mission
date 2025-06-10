package finalmission.repository;

import finalmission.domain.Member;
import finalmission.domain.Reservation;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ReservationRepository extends JpaRepository<Reservation, Long> {
    List<Reservation> findReservationsByMember(Member member);
}
