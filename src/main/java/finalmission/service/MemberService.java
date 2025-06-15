package finalmission.service;

import finalmission.domain.Gym;
import finalmission.domain.Member;
import finalmission.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

@RequiredArgsConstructor
@Service
public class MemberService {

    private final MemberRepository memberRepository;
    private final GymService gymService;

    @Value("${randommer.api-key}")
    private String randommorApiKey;

    public Long addMember(String name, String phoneNumber, String password, Long gymId) {
        final boolean phoneNumberExist = memberRepository.existsByPhoneNumber(phoneNumber);
        if (phoneNumberExist) {
            throw new IllegalArgumentException("이미 가입한 핸드폰 번호입니다.");
        }
        final Gym gym = gymService.getGymById(gymId);
        RestClient restClient = RestClient.builder().build();
        final String[] body = restClient.get()
                .uri("https://randommer.io/api/Name?nameType={type}&quantity={quantity}", "fullname", 1)
                .header("X-API-KEY", randommorApiKey)
                .retrieve()
                .body(String[].class);
        if (body == null) {
            throw new IllegalArgumentException("닉네임 생성 중 에러가 발생하였습니다.");
        }
        final String nickname = body[0];
        final Member signupMember = Member.createSignupMember(name, nickname, phoneNumber, password, gym);
        return memberRepository.save(signupMember).getId();
    }

    public void authenticate(String phoneNumber, String password) {
        final Member member = findMemberByPhoneNumber(phoneNumber);
        if (!member.getPassword().equals(password)) {
            throw new IllegalArgumentException("비밀번호가 일치하지 않습니다.");
        }
    }

    public Member findMemberByPhoneNumber(String phoneNumber) {
        return memberRepository.findMemberByPhoneNumber(phoneNumber)
                .orElseThrow(() -> new IllegalArgumentException("회원을 찾을 수 없습니다."));
    }

    public Member findMemberById(Long id) {
        return memberRepository.findById(id).orElseThrow(() -> new IllegalArgumentException("회원을 찾을 수 없습니다."));
    }
}
