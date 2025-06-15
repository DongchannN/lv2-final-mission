package finalmission.service;

import static org.assertj.core.api.Assertions.assertThat;

import finalmission.domain.Gym;
import finalmission.domain.Member;
import finalmission.repository.GymRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

@ActiveProfiles("test")
@SpringBootTest
class MemberServiceTest {

    @Autowired
    private MemberService memberService;
    @Autowired
    private GymRepository gymRepository;

    private Gym gym;

    @BeforeEach
    void setUp() {
        gym = gymRepository.save(new Gym("gym1", "location1", "01099999999"));
    }

    @Test
    @DisplayName("회원가입 정상 작동")
    void addMemberTest() {
        // given
        String name = "chan";
        String phoneNumber = "01012341234";
        String password = "1234";
        Long gymId = gym.getId();

        // when
        final Long id = memberService.addMember(name, phoneNumber, password, gymId);

        // then
        final Member saved = memberService.findMemberById(id);
        assertThat(saved).isNotNull();
        assertThat(saved.getNickname()).isNotNull();
        assertThat(saved.getPhoneNumber()).isEqualTo(phoneNumber);
        assertThat(saved.getName()).isEqualTo(name);
    }
}