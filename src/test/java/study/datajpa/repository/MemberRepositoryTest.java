package study.datajpa.repository;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Slice;
import org.springframework.data.domain.Sort;
import org.springframework.test.annotation.Rollback;
import org.springframework.transaction.annotation.Transactional;
import study.datajpa.dto.MemberDto;
import study.datajpa.entity.Member;
import study.datajpa.entity.Team;

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


}