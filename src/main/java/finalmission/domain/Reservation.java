package finalmission.domain;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import java.time.LocalDate;
import java.time.LocalTime;

/**
 *
 - 예약 (reservation)
 - 예약한 시간 (time)
 - 예약한 날짜 (date)
 - 예약한 선생 (trainer_id)
 - 예약한 회원 (member_id)
 - 예약한 헬스장 (gym_id)
 */
@Entity
public class Reservation {

    @Id
    private Long id;

    @ManyToOne
    @JoinColumn(name = "gym_id")
    private Gym gym;

    @ManyToOne
    @JoinColumn(name = "member_id")
    private Member member;

    @ManyToOne
    @JoinColumn(name = "trainer_id")
    private Trainer trainer;

    private LocalDate date;

    private LocalTime time;
}
