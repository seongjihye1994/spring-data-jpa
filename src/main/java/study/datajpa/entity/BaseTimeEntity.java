package study.datajpa.entity;

import lombok.Getter;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import javax.persistence.Column;
import javax.persistence.EntityListeners;
import javax.persistence.MappedSuperclass;
import java.time.LocalDateTime;

@EntityListeners(AuditingEntityListener.class) // 이벤트 기반 동작
@MappedSuperclass
@Getter
public class BaseTimeEntity {

    @CreatedDate // 필수!
    @Column(updatable = false)
    private LocalDateTime createdDate;

    @LastModifiedDate // 필수!
    private LocalDateTime lastModifiedDate;
}
