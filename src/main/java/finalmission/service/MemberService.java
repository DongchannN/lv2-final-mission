package finalmission.service;

import finalmission.domain.Gym;
import finalmission.domain.Member;
import finalmission.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class MemberService {

    private final MemberRepository memberRepository;
    private final GymService gymService;

    public void addMember(String name, String nickname, String phoneNumber, String password, Long gymId) {
        final boolean phoneNumberExist = memberRepository.existsByPhoneNumber(phoneNumber);
        if (phoneNumberExist) {
            throw new IllegalArgumentException("이미 가입한 핸드폰 번호입니다.");
        }
        final Gym gym = gymService.getGymById(gymId);
        final Member signupMember = Member.createSignupMember(name, nickname, phoneNumber, password, gym);
        memberRepository.save(signupMember);
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
