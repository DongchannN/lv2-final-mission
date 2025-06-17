package finalmission.controller.dto;

public record UpdateTrainerInfoRequest(String name, int creditPrice, String description, String imageUrl, Long gymId) {
}
