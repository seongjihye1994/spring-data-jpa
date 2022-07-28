package study.datajpa.repository;

import org.springframework.beans.factory.annotation.Value;

public interface UsernameOnly {

//    @Value("#{target.username + ' ' + target.age}")
    String getUsername(); // get + 필드명 -> 특정 필드만 조회
}
