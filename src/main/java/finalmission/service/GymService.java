package finalmission.service;

import finalmission.domain.Gym;
import finalmission.repository.GymRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class GymService {

    private final GymRepository gymRepository;

    public Gym getGymById(Long gymId) {
        return gymRepository.findById(gymId)
                .orElseThrow(() -> new IllegalArgumentException("헬스장이 존재하지 않습니다."));
    }
}
