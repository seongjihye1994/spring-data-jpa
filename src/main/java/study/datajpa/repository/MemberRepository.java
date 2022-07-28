package study.datajpa.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.query.Param;
import study.datajpa.dto.MemberDto;
import study.datajpa.entity.Member;

import javax.persistence.LockModeType;
import javax.persistence.QueryHint;
import java.util.List;
import java.util.Optional;

public interface MemberRepository extends JpaRepository<Member, Long>, MemberRepositoryCustom, JpaSpecificationExecutor {
    // 엔티티, PK

    List<Member> findByUsernameAndAgeGreaterThan(String username, int age);

    @Query(name = "Member.findByUsername")
    List<Member> findByUsername(@Param("username") String username);

    // Spring Data JPA 가 제공하는 리포지토리 메소드에 쿼리 정의 기능!!!
    @Query("select m from Member m where m.username = :username and m.age = :age")
    List<Member> findMember(@Param("username") String username, @Param("age") int age);

    @Query("select m.username from Member m")
    List<String> findUsernameList();

    // DTO로 조회하고 반환할 때는 'new' 키워드로 객체를 생성하듯이 쿼리를 작성해야 한다.
    @Query("select new study.datajpa.dto.MemberDto(m.id, m.username, t.name) from Member m join m.team t")
    List<MemberDto> findMemberDto();

    @Query("select m from Member m where m.username in :names")
    List<Member> findByNames(@Param("names") List<String> names);

    List<Member> findListByUsername(String username); // 컬렉션
    Member findMemberByUsername(String username); // 단건
    Optional<Member> findOptionalByUsername(String username); // 단건 Optional

    // 스프링 데이터 JPA가 제공하는 Page 객체를 사용하면, 페이징을 쉽게 처리할 수 있다.
//    Slice<Member> findByAge(int age, Pageable pageable);
    @Query(value = "select m from Member m left join m.team t",
            countQuery = "select count(m) from Member m")
    Page<Member> findByAge(int age, Pageable pageable);

    @Modifying(clearAutomatically = true) // 이 어노테이션이 있어야 executeUpdate() 를 실행할 수 있다.
    @Query("update Member m set m.age = m.age + 1 where m.age >= :age")
    int bulkAgePlus(@Param("age") int age);

    @Query("select m from Member m left join fetch m.team")
    List<Member> findMemberFetchJoin();

    // 1. 공통 메서드 오러바이드에 @EntityGraph 사용
    @Override
    @EntityGraph(attributePaths = {"team"}) // 멤버 조회 시 연관된 team도 페치조인 하고싶은데, JPQL은 사용하고 싶지 않을 때
    List<Member> findAll();

    // 2. JPQL + 엔티티 그래프
//    @EntityGraph(attributePaths = {"team"}) // 두개 같이 사용해도 됨
    @EntityGraph("Member.all") // 엔티티에 @NamedEntityGraph 를 사용할 때 지정해줌
    @Query("select m from Member m")
    List<Member> findMemberEntityGraph();

    // 3. 메서드 이름으로 쿼리에서 특히 편리하다.
    @EntityGraph(attributePaths = {"team"})
    List<Member> findEntityGraphByUsername(String username);

    @QueryHints(value = @QueryHint(name = "org.hibernate.readOnly", value = "true"))
    Member findReadOnlyByUsername(String username);

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    List<Member> findLockByUsername(String username);

    // Projections 를 사용한 MemberRepository
    List<UsernameOnlyDto> findProjectionsByUsername(@Param("username") String username);

    // nativeQuery 사용
    @Query(value = "select * from member where username = ?", nativeQuery = true)
    Member findByNativeQuery(String username);

    @Query(value = "select m.member_id as id, m.username, t.name as teamName from member m left join team t",
            countQuery = "select count(*) from member",
            nativeQuery = true)
    Page<MemberProjection> findByNativeProjection(Pageable pageable);

}
