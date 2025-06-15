package finalmission.controller.dto;

public record SignupRequest(
        String name,
        String phoneNumber,
        String password,
        Long gymId
) {
}
