package finalmission.controller.dto;

public record TrainerSignupRequest(String name, String phoneNumber, String password, int creditPrice, String description, String imageUrl, Long gymId) {
}
