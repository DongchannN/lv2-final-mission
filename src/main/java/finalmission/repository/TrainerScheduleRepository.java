package finalmission.repository;

import finalmission.domain.Trainer;
import finalmission.domain.TrainerSchedule;
import java.time.LocalTime;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TrainerScheduleRepository extends JpaRepository<TrainerSchedule, Long> {
    List<TrainerSchedule> findTrainerSchedulesByTrainer(Trainer trainer);

    LocalTime time(LocalTime time);
}
