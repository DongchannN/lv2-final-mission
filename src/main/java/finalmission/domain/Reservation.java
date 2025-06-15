package finalmission.domain;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import java.time.LocalDate;
import java.time.LocalTime;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
public class Reservation {

    @GeneratedValue(strategy = GenerationType.IDENTITY)
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

    private ReservationStatus status;

    public Reservation(Gym gym, Member member, Trainer trainer, LocalDate date, LocalTime time, ReservationStatus reservationStatus) {
        this.gym = gym;
        this.member = member;
        this.trainer = trainer;
        this.date = date;
        this.time = time;
        this.status = reservationStatus;
    }

    public static Reservation createAcceptedReservation(Gym gym, Member member, Trainer trainer, LocalDate date, LocalTime time) {
        return new Reservation(gym, member, trainer, date, time, ReservationStatus.ACCEPTED);
    }

    public static Reservation createPendingReservation(Gym gym, Member member, Trainer trainer, LocalDate date, LocalTime time) {
        return new Reservation(gym, member, trainer, date, time, ReservationStatus.PENDING);
    }

    public void update(Gym gym, Trainer trainer, LocalDate date, LocalTime time, ReservationStatus reservationStatus) {
        if (this.gym.getId().equals(gym.getId())
                && this.trainer.getId().equals(trainer.getId())
                && this.date.equals(date)
                && this.time.equals(time)) {
            throw new IllegalArgumentException("시간을 변경하지 않았습니다.");
        }
        this.gym = gym;
        this.trainer = trainer;
        this.date = date;
        this.time = time;
        this.status = reservationStatus;
    }

    public int calculateTrainerCreditDifference(Trainer newTrainer) {
        return newTrainer.getCreditPrice() - this.trainer.getCreditPrice();
    }

    public void accept() {
        this.status = ReservationStatus.ACCEPTED;
    }

    public void refund() {
        this.member.increaseCredit(this.trainer.getCreditPrice());
    }
}
