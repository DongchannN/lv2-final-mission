package finalmission.controller.dto;

public record SignupRequest(
        String name,
        String nickname,
        String phoneNumber,
        String password
) {
}
