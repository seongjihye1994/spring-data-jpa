package study.datajpa.entity;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;

@Entity
@Getter
@Setter
public class Member {

    @Id @GeneratedValue // JPA가 자동으로 ID를 생성해서 넣어줌
    private Long id;
    private String username;

    // JPA 사용시 Entity는 기본 생성자 필수 -> Proxy 기술 사용을 위해 protected 으로 설정.
    protected Member() {
    }

    public Member(String username) {
        this.username = username;
    }
}
