package study.datajpa.entity;

import lombok.*;

import javax.persistence.*;

@Entity
@Getter
@Setter
@NoArgsConstructor(access = AccessLevel.PROTECTED) // JPA 사용시 Entity는 기본 생성자 필수 -> Proxy 기술 사용을 위해 protected 으로 설정.
@ToString(of = {"id", "username", "age"}) // toString은 연관관계가 없는 필드에만 설정하는 것이 좋다.
@NamedQuery(
        name="Member.findByUsername",
        query="select m from Member m where m.username = :username"
)
public class Member {

    @Id
    @GeneratedValue // JPA가 자동으로 ID를 생성해서 넣어줌
    @Column(name = "member_id")
    private Long id;
    private String username;
    private int age;

    // 양방향 연관관계 설정
    @ManyToOne(fetch = FetchType.LAZY) // ManyToOne 관계는 지연로딩으로 설정!!
    @JoinColumn(name = "team_id") // fk
    private Team team;

    // JPA 사용시 Entity는 기본 생성자 필수 -> Proxy 기술 사용을 위해 protected 으로 설정.
/*    protected Member() {
    }*/

    public Member(String username) {
        this.username = username;
    }

    public Member(String username, int age) {
        this.username = username;
        this.age = age;
    }

    // Member를 생성할 때 생성자를 통해
    public Member(String username, int age, Team team) {
        this.username = username;
        this.age = age;

        if (team != null) {
            // 양방향 연관관계 (세팅) 편의 메소드를 사용해 해당 객체(Member)의 Team 을 수정한다.
            changeTeam(team);
        }
    }

    // 연관관계 (세팅)편의 메소드 -> Member 는 Team을 변경할 수 있다!
    public void changeTeam(Team team) {
        this.team = team; // 나(this:Member)의 팀을 변경한다.
        team.getMembers().add(this); // 팀에서도 나(this:Member)를 수정하도록 한다.
    }
}
