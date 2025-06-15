package finalmission.controller;

import finalmission.controller.dto.SigninRequest;
import finalmission.controller.dto.SignupRequest;
import finalmission.global.LoginMember;
import finalmission.service.JwtService;
import finalmission.service.MemberService;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class MemberController {

    public static final String JWT_PREFIX = "Bearer ";
    private final MemberService memberService;
    private final JwtService jwtService;

    public MemberController(MemberService memberService, JwtService jwtService) {
        this.memberService = memberService;
        this.jwtService = jwtService;
    }

    @PostMapping("/api/member/signup")
    public ResponseEntity<Void> signup(@RequestBody SignupRequest signupRequest) {
        memberService.addMember(
                signupRequest.name(),
                signupRequest.phoneNumber(),
                signupRequest.password(),
                signupRequest.gymId()
        );
        return ResponseEntity.ok().build();
    }

    @PostMapping("/api/member/signin")
    public ResponseEntity<Void> login(@RequestBody SigninRequest signinRequest, HttpServletResponse response) {
        final Long memberId = memberService.authenticate(signinRequest.phoneNumber(), signinRequest.password());
        final String token = jwtService.generateToken(memberId.toString(), "ROLE_USER");
        response.setHeader("Authentication", JWT_PREFIX + token);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/api/member/test")
    public ResponseEntity<String> test(@Schema(hidden = true) LoginMember loginMember) {
        return ResponseEntity.ok(loginMember.id().toString());
    }
}
