package study.datajpa.repository;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.Rollback;
import org.springframework.transaction.annotation.Transactional;
import study.datajpa.entity.Member;

import static org.assertj.core.api.Assertions.assertThat;

@Transactional
@SpringBootTest // 스프링 빈 인젝션을 위해 스프링 컨테이너가 필요함, JUNIT5 + Spring boot 환경에서는 RunWith 필요 x
@Rollback(false) // 스프링 부트는 트랜잭셔널이 붙으면 테스트시 결과를 롤백을 시켜버린다. 결과를 보고싶으면 Rollback을 false로. -> 커밋
class MemberJpaRepositoryTest {

    @Autowired
    MemberJpaRepository memberJpaRepository;

    @Test
    void testMember() {
        Member member = new Member("memberA");
        Member savedMember = memberJpaRepository.save(member);

        Member findMember = memberJpaRepository.find(savedMember.getId());

        assertThat(findMember.getId()).isEqualTo(member.getId());
        assertThat(findMember.getUsername()).isEqualTo(member.getUsername());
        assertThat(findMember).isEqualTo(member);
    }

    @Test
    void find() {
    }
}