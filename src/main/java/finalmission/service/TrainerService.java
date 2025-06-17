package finalmission.service;

import finalmission.controller.dto.TrainerResponse;
import finalmission.domain.Gym;
import finalmission.domain.Trainer;
import finalmission.repository.TrainerRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class TrainerService {

    private final TrainerRepository trainerRepository;
    private final GymService gymService;

    public TrainerResponse getTrainerInfoById(Long trainerId) {
        return TrainerResponse.from(getTrainerById(trainerId));
    }

    @Transactional
    public void updateTrainer(Long trainerId, String name, int creditPrice, String description, String imageUrl, Long gymId) {
        final Trainer trainer = getTrainerById(trainerId);
        final Gym gym = gymService.getGymById(gymId);
        trainer.update(name, creditPrice, description, imageUrl, gym);
    }

    public Trainer getTrainerById(Long trainerId) {
        return trainerRepository.findById(trainerId)
                .orElseThrow(() -> new IllegalArgumentException("트레이너가 존재하지 않습니다."));
    }
}
