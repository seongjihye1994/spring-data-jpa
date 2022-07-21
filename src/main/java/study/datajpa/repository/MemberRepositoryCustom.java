package study.datajpa.repository;

import study.datajpa.entity.Member;

import java.util.List;

/**
 * 스프링 데이터 JPA가 제공하는 인터페이스를 사용하지 않고, 메서드를 직접 만들어서 사용해보자.
 */
public interface MemberRepositoryCustom {

    List<Member> findMemberCustom();
}
