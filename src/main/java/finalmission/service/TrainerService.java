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

    public Long authenticate(String phoneNumber, String password) {
        final Trainer trainer = trainerRepository.findTrainerByPhoneNumber(phoneNumber)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 트레이너입니다."));
        if (!trainer.getPassword().equals(password)) {
            throw new IllegalArgumentException("인증에 실패하였습니다.");
        }
        return trainer.getId();
    }

    public Long addTrainer(String name, String phoneNumber, String password, int creditPrice, String description, String imageUrl, Long gymId) {
        final Gym gym = gymService.getGymById(gymId);
        if (trainerRepository.existsByPhoneNumber(phoneNumber)) {
            throw new IllegalArgumentException("이미 등록된 전화번호입니다.");
        }
        Trainer trainer = new Trainer(name, phoneNumber, password, creditPrice, description, imageUrl, gym);
        return trainerRepository.save(trainer).getId();
    }

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
