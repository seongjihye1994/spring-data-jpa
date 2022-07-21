package study.datajpa.entity;

import lombok.Getter;

import javax.persistence.Column;
import javax.persistence.MappedSuperclass;
import javax.persistence.PrePersist;
import javax.persistence.PreUpdate;
import java.time.LocalDateTime;

@Getter
@MappedSuperclass // 클래스를 상속받는게 아닌, 클래스 내부의 속성(필드)만 상속받아서 사용하는 기능
public class JpaBaseEntity {


    @Column(updatable = false) // 업데이트 시에는 적용 x
    private LocalDateTime createdDate;
    private LocalDateTime updatedDate;

    @PrePersist // Persist (저장) 전에 작동하는 메소드
    public void perPersist() {
        LocalDateTime now = LocalDateTime.now();
        createdDate = now; // 등록일과 수정일을 등록할 때 넣어놓으면 유지보수 측면에서 편하다.
        updatedDate = now;
    }

    @PreUpdate // update 전에 작동하는 메소드
    public void preUpdate() {
        updatedDate = LocalDateTime.now();
    }
}
