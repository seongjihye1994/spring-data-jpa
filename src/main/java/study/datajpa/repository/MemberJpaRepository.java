package study.datajpa.repository;

import org.springframework.stereotype.Repository;
import study.datajpa.entity.Member;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

@Repository
public class MemberJpaRepository {

    // 스프링 컨테이너 구동 시 영속성 컨텍스트에서 자동으로 값을 가져옴
    @PersistenceContext // JPA 사용을 위한 EntityManager 사용을 위해
    private EntityManager em;

    public Member save(Member member) {
        em.persist(member);
        return member;
    }

    public Member find(Long id) {
        return em.find(Member.class, id);
    }

}
