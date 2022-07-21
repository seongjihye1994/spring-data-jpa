package study.datajpa.entity;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.Rollback;
import org.springframework.transaction.annotation.Transactional;
import study.datajpa.repository.MemberRepository;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.util.List;

@SpringBootTest
@Transactional
@Rollback(false)
class MemberTest {

    @PersistenceContext
    EntityManager em;
    
    @Autowired
    MemberRepository memberRepository;


    @Test
    public void testEntity() {
        Team teamA = new Team("teamA");
        Team teamB = new Team("teamB");

        em.persist(teamA);
        em.persist(teamB);

        Member member1 = new Member("member1", 10, teamA);
        Member member2 = new Member("member2", 20, teamA);
        Member member3 = new Member("member3", 30, teamB);
        Member member4 = new Member("member4", 40, teamB);

        em.persist(member1);
        em.persist(member2);
        em.persist(member3);
        em.persist(member4);

        em.flush(); // flush 강제호출 -> 영속성 컨텍스트 내부의 값을 DB와 싱크 맞추기
        em.clear(); // 영속성 컨텍스트 내부를 모두 삭제함

        // 확인 -> 현재 영속성 컨텍스트가 모두 비워졌기 때문에 DB에서 먼저 조회해온 후 영속성 컨텍스트에 저장하고 값을 출력한다.
        List<Member> members = em.createQuery("select m from Member m", Member.class)
                .getResultList();

        for (Member member : members) {
            System.out.println("member = " + member);
            System.out.println("-> member.getTeam() = " + member.getTeam());
        }
    }

    @Test
    public void JpaEventBaseEntity() throws InterruptedException {
        // given
        Member member = new Member("member1");
        memberRepository.save(member); // @PrePersist 작동 -> 영속성 컨텍스트에 저장

        Thread.sleep(100);
        member.setUsername("member2"); // 이름 수정

        em.flush(); // @PreUpdate 작동 -> DB에 저장
        em.clear();

        // when
        Member findMember = memberRepository.findById(member.getId()).get();

        // then
        System.out.println("findMember.createDate = " + findMember.getCreatedDate());
        // findMember.createDate = 2022-07-21T11:15:47.601819
        System.out.println("findMember.updateDate = " + findMember.getLastModifiedDate());
        // findMember.updateDate = 2022-07-21T11:15:47.778844
        System.out.println("findMember.createdBy = " + findMember.getCreatedBy());
        // findMember.createdBy = 68c756d2-f551-4dfa-93d4-1e3576e545bf
        System.out.println("findMember.modifiedBy = " + findMember.getLastModifiedBy());
        // findMember.modifiedBy = 1aa6acce-914d-4124-9c41-4fdbddc064ce

    }
}