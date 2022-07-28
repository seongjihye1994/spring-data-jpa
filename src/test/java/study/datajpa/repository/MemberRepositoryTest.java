package study.datajpa.repository;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.*;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.test.annotation.Rollback;
import org.springframework.transaction.annotation.Transactional;
import study.datajpa.dto.MemberDto;
import study.datajpa.entity.Member;
import study.datajpa.entity.Team;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@Transactional
@Rollback(false)
class MemberRepositoryTest {

    @Autowired MemberRepository memberRepository;
    @Autowired TeamRepository teamRepository;
    @PersistenceContext EntityManager em;

    @Test
    void testMember() {
        Member member = new Member("memberA");
        Member savedMember = memberRepository.save(member);
        // MemberRepository 인터페이스가 상속받은 JpaRepository 인터페이스가
        // 제공하는 메소드

        Member findMember = memberRepository.findById(savedMember.getId()).get();

        assertThat(findMember.getId()).isEqualTo(member.getId());
        assertThat(findMember.getUsername()).isEqualTo(member.getUsername());
        assertThat(findMember).isEqualTo(member);
    }

    @Test
    public void basicCRUD() {
        Member member1 = new Member("member1");
        Member member2 = new Member("member2");

        memberRepository.save(member1);
        memberRepository.save(member2);

        // 단건 조회 검증
        Member findMember1 = memberRepository.findById(member1.getId()).get();
        Member findMember2 = memberRepository.findById(member2.getId()).get();

        assertThat(findMember1).isEqualTo(member1);
        assertThat(findMember2).isEqualTo(member2);

        // findMember1.setUsername("member!!!!!!");
        // member 객체 생성 시 member 이름을 member1로 저장했다.
        // 이후 영속성 컨텍스트에서 조회해서 이름을 member!!!!!! 으로 수정했다.
        // 이 때, 영속성 컨텍스트에서는 member 객체의 이름이 바꼈다는 것일 인지(더티체킹)하고
        // 트랜잭션이 커밋될 때, 바뀐 값으로 DB에 쿼리를 날린다.

        // 리스트 조회 검증
        List<Member> all = memberRepository.findAll();
        assertThat(all.size()).isEqualTo(2);

        // 카운트 검증
        long count = memberRepository.count();
        assertThat(count).isEqualTo(2);

        // 삭제 검증
        memberRepository.delete(member1);
        memberRepository.delete(member2);

        long deletedCount = memberRepository.count();
        assertThat(deletedCount).isEqualTo(0);
    }

    @Test
    public void findByUsernameAndAgeGreaterThen() {
        Member m1 = new Member("AAA", 10);
        Member m2 = new Member("AAA", 20);

        memberRepository.save(m1);
        memberRepository.save(m2);

        // 이름 : AAA, 나이 : 15 파라미터로 던짐 -> 이 파라미터와 비교해서 쿼리를 날림
        List<Member> result = memberRepository.findByUsernameAndAgeGreaterThan("AAA", 15);

        assertThat(result.get(0).getUsername()).isEqualTo("AAA");
        assertThat(result.get(0).getAge()).isEqualTo(20);
        assertThat(result.size()).isEqualTo(1);
    }

    @Test
    public void testNamedQuery() {
        Member m1 = new Member("AAA", 10);
        Member m2 = new Member("BBB", 20);

        memberRepository.save(m1);
        memberRepository.save(m2);

        List<Member> result = memberRepository.findByUsername("AAA");
        Member findMember = result.get(0);
        assertThat(findMember).isEqualTo(m1);
    }

    @Test
    public void testQuery() {
        Member m1 = new Member("AAA", 10);
        Member m2 = new Member("BBB", 20);

        memberRepository.save(m1);
        memberRepository.save(m2);

        List<Member> result = memberRepository.findMember("AAA", 10);
        assertThat(result.get(0)).isEqualTo(m1);
    }

    @Test
    public void findUsernameList() {
        Member m1 = new Member("AAA", 10);
        Member m2 = new Member("BBB", 20);

        memberRepository.save(m1);
        memberRepository.save(m2);

        List<String> usernameList = memberRepository.findUsernameList();

        for (String s : usernameList) {
            System.out.println("s = " + s);
        }

    }

    @Test
    public void findMemberDto() {
        Team team = new Team("teamA");
        teamRepository.save(team);

        Member m1 = new Member("AAA", 10);
        m1.setTeam(team); // 연관관계 설정
        memberRepository.save(m1);

        List<MemberDto> memberDto = memberRepository.findMemberDto();

        for (MemberDto dto : memberDto) {
            System.out.println("dto = " + dto);
        }
    }

    @Test
    public void findByNames() {
        Member m1 = new Member("AAA", 10);
        Member m2 = new Member("BBB", 20);

        memberRepository.save(m1);
        memberRepository.save(m2);

        List<Member> result = memberRepository.findByNames(Arrays.asList("AAA", "BBB"));

        for (Member member : result) {
            System.out.println("member = " + member);
        }
    }

    @Test
    public void returnType() {
        Member m1 = new Member("AAA", 10);
        Member m2 = new Member("BBB", 20);

        memberRepository.save(m1);
        memberRepository.save(m2);

        Member findMember = memberRepository.findMemberByUsername("AAA");
        System.out.println("findMember = " + findMember);

        /**
         * 단건 조회 메소드를 호출하면 스프링 데이터 JPA는 내부에서 JPQL의
         * `Query.getSingleResult()` 메서드를 호출한다.
         *
         * 이 메서드를 호출했을 때 **조회 결과가 없으면 NoResultException**
         * **예외가 발생**하는데, 개발자 입장에서 다루기가 상당히 까다롭다.
         *
         * ***스프링 데이터 JPA는 단건을 조회할 때 이 예외가 발생하면***
         * ***예외를 무시하고 대신에 null을 반환***한다.
         */
    }

    @Test
    public void paging() {
        // given
        memberRepository.save(new Member("member1", 10));
        memberRepository.save(new Member("member2", 10));
        memberRepository.save(new Member("member3", 10));
        memberRepository.save(new Member("member4", 10));
        memberRepository.save(new Member("member5", 10));

        int age = 10;

        // 0 페이지부터 3개 가져오기, 정렬은 username을 DESC로
        PageRequest pageRequest = PageRequest.of(0, 3, Sort.by(Sort.Direction.DESC, "username"));

        // when
        Page<Member> page = memberRepository.findByAge(age, pageRequest);

        // Member 엔티티를 그대로 노출하면 XXX!!!
        // DTO로 변환해주자!
        Page<MemberDto> toMap = page.map(member -> new MemberDto(member.getId(), member.getUsername(), null));

        // 반환 타입을 Page로 받으면 totalCount 쿼리를 자동으로 날려준다!

        // then
        List<Member> content = page.getContent();

        assertThat(content.size()).isEqualTo(3); // 한 페이지에 3명의 멤버가 나오는지 확인
        assertThat(page.getTotalElements()).isEqualTo(5); // 총 멤버가 5명인지 확인
        assertThat(page.getNumber()).isEqualTo(0); // 페이지 번호를 자동으로 알려준다!
        assertThat(page.getTotalPages()).isEqualTo(2); // 총 페이지 갯수, 멤버가 5명이니까 한 페이지당 멤버 2명씩 자르면 총 2페이지가 나와야 함.
        assertThat(page.isFirst()).isTrue(); // 첫 페이지인지 확인
        assertThat(page.hasNext()).isTrue(); // 다음 페이지가 있는지 확인

        // Slice 객체는 totalCount 쿼리를 날리지 않는다.
        // 그래서 totalCount 도 모를 뿐더러, totalPages 도 모른다.
    }

    @Test
    public void bulkUpdate() {

        // given
        memberRepository.save(new Member("member1", 10));
        memberRepository.save(new Member("member2", 19));
        memberRepository.save(new Member("member3", 20));
        memberRepository.save(new Member("member4", 21));
        memberRepository.save(new Member("member5", 40));

        // when
        int resultCount = memberRepository.bulkAgePlus(20); // 20살 이상부터 나이에 +1 하기
//        em.flush(); // 영속성 컨텍스트와 DB 싱크맞춤
//        em.clear(); // 영속성 컨텍스트 초기화

        List<Member> result = memberRepository.findByUsername("member5");
        Member member5 = result.get(0);
        System.out.println("member5 = " + member5); // age = 40

        // 벌크 연산은 영속성 컨텍스트를 신경쓰지 않는다.
        // 오로지 DB 업데이트에만 신경 쓸 뿐이다.
        // 그래서 위 코드에서 20살 이상부터 나이에 +1 하기로 인해
        // DB에서는 40이 41로 수정되었지만
        // 영속성 컨텍스트는 여전히 40이다.
        // 그래서 이 메소드가 끝나기 전(트랜잭션 커밋 전)
        // 영속성 컨텍스트의 1차 캐시를 조회해 온 결과 여전히 40살 인 것을 알 수 있다.
        // 그래서 em.flush() 와 em.clear()을 꼭 해줘야 값이 일치된다.

        // then
        assertThat(resultCount).isEqualTo(3);
    }

    @Test
    public void findMemberLazy() {

        // given
        // member1 -> teamA 참조 (연관관계)
        // member2 -> teamB 참조 (연관관계)

        Team teamA = new Team("teamA");
        Team teamB = new Team("teamB");

        teamRepository.save(teamA);
        teamRepository.save(teamB);

        Member member1 = new Member("member1", 10, teamA);
        Member member2 = new Member("member2", 10, teamB);

        memberRepository.save(member1);
        memberRepository.save(member2);

        em.flush();
        em.clear();

        // when N + 1 문제
        // select Member 1
        List<Member> members = memberRepository.findAll(); // @EntityGraph를 사용한 findAll 과 일반 findAll 같이 있음
//        List<Member> members = memberRepository.findMemberFetchJoin();

        for (Member member : members) {
            System.out.println("member = " + member.getUsername());
            // 이 때 member 엔티티의 team 필드는 비어있다. -> 지연관계 설정으로 인해 가짜 객체를 만든다!
            System.out.println("member.teamClass = " + member.getTeam().getClass());

            // Team 이름 조회 -> Team의 필드인 name을 조회하니 Team 을 이 때 터치함! -> 이 때는 진짜 객체를 가져옴.
            System.out.println("member.team = " + member.getTeam().getName());
        }
    }

    @Test
    public void queryHint() {

        // given
        Member member1 = memberRepository.save(new Member("member1", 10));
        em.flush();
        em.clear();

        // when
        Member findMember = memberRepository.findReadOnlyByUsername("member1");
        findMember.setUsername("member2");
        // JPA Hint를 사용해 readOnly 옵션을 줬기 때문에,
        // 더티체킹을 위한 원본과 사본 객체 두개를 만들지 않도록 하여 메모리 낭비를 줄인다.

    }


    @Test
    public void lock() {

        // given
        Member member1 = memberRepository.save(new Member("member1", 10));
        em.flush();
        em.clear();

        // when
        List<Member> result = memberRepository.findLockByUsername("member1");

    }

    @Test
    public void callCustom() {
        List<Member> result = memberRepository.findMemberCustom();
    }

    @Test
    public void specBasic() {

        // given
        Team teamA = new Team("teamA");
        em.persist(teamA);

        Member m1 = new Member("m1", 0, teamA);
        Member m2 = new Member("m2", 0, teamA);
        em.persist(m1);
        em.persist(m2);

        em.flush();
        em.clear();

        // when
        Specification<Member> spec = MemberSpec.username("m1").and(MemberSpec.teamName("teamA"));
        List<Member> result = memberRepository.findAll(spec);

        Assertions.assertThat(result.size()).isEqualTo(1);
    }

    @Test
    public void queryByExample() {

        // given
        Team teamA = new Team("teamA");
        em.persist(teamA);

        Member m1 = new Member("m1", 0, teamA);
        Member m2 = new Member("m2", 0, teamA);
        em.persist(m1);
        em.persist(m2);

        em.flush();
        em.clear();

        // when
        memberRepository.findByUsername("m1");
        // 만약 조건이 여러개로 동적 쿼리가 발생해야 한다면??
        // Query by Example 를 사용해보자.

        // Probe
        Member member = new Member("m1"); // 엔티티 자체가 검색 조건이 된다.
        Team team = new Team("teamA");
        member.setTeam(team); // 연관관계 세팅

        ExampleMatcher matcher = ExampleMatcher.matching()
                .withIgnorePaths("age");// age 라는 속성은 무시한다.

        Example<Member> example = Example.of(member, matcher);// 엔티티로 example를 만든다. matcher 로 무시할 속성은 무시한다.

        List<Member> result = memberRepository.findAll(example);

        assertThat(result.get(0).getUsername()).isEqualTo("m1");

        // queryByExample 은 inner join만 가능하고, outer join은 불가하다.
        // 그래서 실무에서 잘 사용하지 않는다.


    }

    @Test
    public void projections() {

        // given
        Team teamA = new Team("teamA");
        em.persist(teamA);

        Member m1 = new Member("m1", 0, teamA);
        Member m2 = new Member("m2", 0, teamA);
        em.persist(m1);
        em.persist(m2);

        em.flush();
        em.clear();

        // when
        // m2 멤버만 조회하고 싶으면?
        List<UsernameOnlyDto> result = memberRepository.findProjectionsByUsername("m1");

        for (UsernameOnlyDto usernameOnlyDto : result) {
            System.out.println("usernameOnlyDto = " + usernameOnlyDto.getUsername());
        }
    }

    @Test
    public void nativeQuery() {

        // given
        Team teamA = new Team("teamA");
        em.persist(teamA);

        Member m1 = new Member("m1", 0, teamA);
        Member m2 = new Member("m2", 0, teamA);
        em.persist(m1);
        em.persist(m2);

        em.flush();
        em.clear();

        // when
        Page<MemberProjection> result = memberRepository.findByNativeProjection(PageRequest.of(0, 10));

        List<MemberProjection> content = result.getContent();

        for (MemberProjection memberProjection : content) {
            System.out.println("memberProjection.getUsername() = " + memberProjection.getUsername());
            System.out.println("memberProjection.getTeamName() = " + memberProjection.getTeamName());
        }
    }






}