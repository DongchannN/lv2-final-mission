package finalmission.controller.dto;

import finalmission.domain.Trainer;

public record TrainerResponse(String name, String description, String imageUrl, int creditPrice, Long gymId, String gymName) {

    public static TrainerResponse from(Trainer trainer) {
        return new TrainerResponse(
                trainer.getName(),
                trainer.getDescription(),
                trainer.getImageUrl(),
                trainer.getCreditPrice(),
                trainer.getGym().getId(),
                trainer.getGym().getName()
        );
    }
}
