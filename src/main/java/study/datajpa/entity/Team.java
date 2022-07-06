package study.datajpa.entity;

import lombok.*;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@Setter
@NoArgsConstructor(access = AccessLevel.PROTECTED) // JPA 사용시 Entity는 기본 생성자 필수 -> Proxy 기술 사용을 위해 protected 으로 설정.
@ToString(of = {"id", "name"}) // toString은 연관관계가 없는 필드에만 설정하는 것이 좋다.
public class Team {

    @Id
    @GeneratedValue
    @Column(name = "tema_id")
    private Long id;
    private String name; // 팀이름

    // 양방향 연관관계 설정
    @OneToMany(mappedBy = "team") // fk가 없는 쪽에 mappedBy 설정
    private List<Member> members = new ArrayList<>();

    public Team(String name) {
        this.name = name;
    }
}
