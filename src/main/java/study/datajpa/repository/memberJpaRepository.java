package study.datajpa.repository;

import org.springframework.stereotype.Repository;
import study.datajpa.entity.Member;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.util.List;
import java.util.Optional;

@Repository
public class memberJpaRepository {

    // 스프링 컨테이너 구동 시 영속성 컨텍스트에서 자동으로 값을 가져옴
    @PersistenceContext // JPA 사용을 위한 EntityManager 사용을 위해
    private EntityManager em;

    public Member save(Member member) {
        em.persist(member);
        return member;
    }

    public void delete(Member member) {
        em.remove(member);
    }

    public List<Member> findAll() {
        // JPQL 사용
        return em.createQuery("select m from Member m", Member.class)
                .getResultList();
    }

    public Optional<Member> findById(Long id) {
        Member member = em.find(Member.class, id);
        return Optional.ofNullable(member);
    }

    public long count() {
        return em.createQuery("select count(m) from Member m", Long.class)
                .getSingleResult();
    }

    public Member find(Long id) {
        return em.find(Member.class, id);
    }

    // 메소드 이름을 분석해서 JPQL 쿼리 실행
    public List<Member> findByUsernameAndAgeGreaterThan(String username, int age) {
        return em.createQuery("select m from Member m where m.username = :username and m.age > :age")
                .setParameter("username", username)
                .setParameter("age", age)
                .getResultList();
    }

    public List<Member> findByUsername(String username) {
        return em.createNamedQuery("Member.findByUsername", Member.class)
                .setParameter("username", username)
                .getResultList();
    }

    /**
     * 검색 조건: 나이가 10살
     * 정렬 조건: 이름으로 내림차순
     * 페이징 조건: 첫 번째 페이지, 페이지당 보여줄 데이터는 3건
     *
     * @param age
     * @param offset
     * @param limit
     * @return
     */
    public List<Member> findByPage(int age, int offset, int limit) {
        return em.createQuery("select m from Member m where m.age = :age order by m.username desc")
                .setParameter("age", age)
                .setFirstResult(offset) // 어디서부터(offset) 데이터를 가져올 지 정한다.
                .setMaxResults(limit) // 어디까지(limit) 데이터를 가져올 지 정한다.
                .getResultList();
    }

    // 현재 내 페이지가 몇번째 페이지인지를 알기 위해 totalCount를 가져온다.
    public long totalCount(int age) {
        return em.createQuery("select count(m) from Member m where m.age = :age", Long.class)
                .setParameter("age", age)
                .getSingleResult(); // 갯수는 한 건만 조회되니 singleResult
    }

    // 벌크성 수정 쿼리 - 대용량 더티 체킹
    public int bulkAgePlus(int age) {

        int resultCount = em.createQuery("update Member m set m.age = m.age + 1" +
                        " where m.age >= :age")
                .setParameter("age", age)
                .executeUpdate();

        return resultCount;

    }

}
