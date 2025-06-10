package finalmission.domain;

import jakarta.persistence.Entity;
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

    public Reservation(Gym gym, Member member, Trainer trainer, LocalDate date, LocalTime time) {
        this.gym = gym;
        this.member = member;
        this.trainer = trainer;
        this.date = date;
        this.time = time;
    }

    public void update(Gym gym, Trainer trainer, LocalDate date, LocalTime time) {
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
    }
}
