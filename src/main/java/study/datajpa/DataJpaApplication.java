package study.datajpa;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.data.domain.AuditorAware;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

import java.util.Optional;
import java.util.UUID;

@EnableJpaAuditing // Spring Data JPA가 제공하는 Auditing 기능을 사용하기 위한 설정
@SpringBootApplication
public class DataJpaApplication {

	public static void main(String[] args) {
		SpringApplication.run(DataJpaApplication.class, args);
	}

	// Spring Data JPA가 제공하는 Auditing 기능 중 사용자와 수정자 기능을 사용하기 위한 설정
	@Bean
	public AuditorAware<String> auditorProvider() { // 등록 및 수정이 발생할 때 마다 이 메소드가 호출돼서 값이 채워진다.
		return () -> Optional.of(UUID.randomUUID().toString());
	}

}
