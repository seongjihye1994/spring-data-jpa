package study.datajpa.repository;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import study.datajpa.entity.Item;

@SpringBootTest
public class ItemRepositoryTest {

    @Autowired ItemRepository itemRepository;

    @Test
    void save() {

        Item item = new Item("A"); // 임의의 A 라는 값을 PK로 생성자로 설정해서 객체를 생성함!
        itemRepository.save(item); // 저장!

        /**
         * Entity의 @GeneratedValue는
         * JPA가 save를 할 때,
         * 영속성 컨텍스트 내부에서 값이 생성된다.
         *
         * 즉, 그 전까지는 null이다.
         *
         * -> persist를 호출해서 저장한다.
         */

        /**
         * Entity의 PK를 객체 생성과 동시에 설정했다.
         * 즉, null이 아니다.
         *
         * merge를 호출해서 병합한다.
         */
    }
}
