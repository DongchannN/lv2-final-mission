package finalmission.repository;

import finalmission.domain.Trainer;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TrainerRepository extends JpaRepository<Trainer, Long> {

    Optional<Trainer> findTrainerByPhoneNumber(String phoneNumber);

    boolean existsByPhoneNumber(String phoneNumber);
}
