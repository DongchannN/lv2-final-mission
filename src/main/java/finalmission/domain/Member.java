package finalmission.domain;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * - 회원 (member)
 * 	- 등급 : 일반 회원, 헬스장 등록 회원 (grade)
 * 	- 크레딧 얼마나 가지고 있는지 (creditAmount)
 * 	- 닉네임 (nickname)
 * 	- 본명 (name)
 * 	- 전화번호 (phoneNumber)
 * 	- 비밀번호 (password)
 * 	- 속한 헬스장 (gym_id)
 */
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
public class Member {

    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Id
    private Long id;

    private String name;

    private String nickname;

    private String phoneNumber;

    private String password;

    private int creditAmount;

    @OneToOne
    @JoinColumn(name = "gym_id")
    private Gym gym;

    public Member(String name, String nickname, String phoneNumber, String password, int creditAmount, Gym gym) {
        this.name = name;
        this.nickname = nickname;
        this.phoneNumber = phoneNumber;
        this.password = password;
        this.creditAmount = creditAmount;
    }

    public static Member createSignupMember(String name, String nickname, String phoneNumber, String password, Gym gym) {
        return new Member(name, nickname, phoneNumber, password, 0, gym);
    }

    public void decreaseCredit(int creditPrice) {
        this.creditAmount -= creditPrice;
    }
}
