package finalmission.repository;

import finalmission.domain.Member;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MemberRepository extends JpaRepository<Member, Long> {
    Optional<Member> findMemberByPhoneNumber(String phoneNumber);

    boolean existsByPhoneNumber(String phoneNumber);
}
