package com.delivery.jpa_delivery.service;

import com.delivery.jpa_delivery.dto.StoreDTO;
import com.delivery.jpa_delivery.entity.Store;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.Commit;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@SpringBootTest
@Transactional
class StoreServiceTest {

    @PersistenceContext
    EntityManager em;

    // =========================================================
    // 1. 가게 저장
    // =========================================================

    @Test
    @DisplayName("가게 저장 - persist")
    @Commit
    void testSave() {
        Store 맛나분식 = Store.builder()
                .name("맛나분식")
                .address("서울 강남구")
                .build();

        em.persist(맛나분식);

        System.out.println("저장된 가게: " + 맛나분식);

        /**
         * 실행 결과 (DB)
         *
         * select * from store;
         * +----+----------+-----------+
         * | id | name     | address   |
         * +----+----------+-----------+
         * |  1 | 맛나분식 | 서울 강남구 |
         * +----+----------+-----------+
         */
    }

    // =========================================================
    // 2. 가게 단건 조회
    // =========================================================

    @Test
    @DisplayName("가게 단건 조회 - em.find()")
    @Commit
    void testFind() {
        // 저장
        Store 피자집 = Store.builder()
                .name("피자집")
                .address("서울 마포구")
                .build();
        em.persist(피자집);

        em.flush();  // INSERT SQL 실행
        em.clear();  // 1차 캐시 초기화 → DB에서 다시 조회

        // 조회
        Store found = em.find(Store.class, 피자집.getId());
        System.out.println("조회된 가게: " + found);

        /**
         * em.flush() → SQL 실행
         * em.clear() → 1차 캐시 비움
         * em.find()  → DB에서 SELECT 후 1차 캐시에 올림
         */
    }

    // =========================================================
    // 3. 전체 가게 목록 조회 (JPQL)
    // =========================================================

    @Test
    @DisplayName("전체 가게 목록 조회 - JPQL")
    @Commit
    void testFindAll() {
        em.persist(Store.builder().name("맛나분식").address("서울 강남구").build());
        em.persist(Store.builder().name("피자헛").address("서울 마포구").build());
        em.persist(Store.builder().name("초밥집").address("서울 종로구").build());

        em.flush();
        em.clear();

        List<Store> stores = em.createQuery("SELECT s FROM Store s", Store.class)
                .getResultList();

        System.out.println("전체 가게 수: " + stores.size());
        stores.forEach(s -> System.out.println("  - " + s));
    }

    // =========================================================
    // 4. 가게 삭제
    // =========================================================

    @Test
    @DisplayName("가게 삭제 - em.remove()")
    @Commit
    void testDelete() {
        Store 국밥집 = Store.builder()
                .name("국밥집")
                .address("서울 종로구")
                .build();
        em.persist(국밥집);
        em.flush();

        Long id = 국밥집.getId();

        // 삭제
        em.remove(국밥집);
        em.flush();

        Store deleted = em.find(Store.class, id);
        System.out.println("삭제 후 조회 결과: " + deleted); // null

        /**
         * CascadeType.ALL 설정 후 Menu가 합류하면
         * 가게 삭제 시 소속 메뉴도 자동으로 삭제됨 (추후 확인 예정)
         */
    }

    // =========================================================
    // 5. DTO 변환 - 순환 참조 방지
    // =========================================================

    @Test
    @DisplayName("StoreDTO.from() - Entity → DTO 변환 (순환 참조 방지)")
    void testDtoConversion() {
        Store 파스타집 = Store.builder()
                .name("파스타집")
                .address("서울 용산구")
                .build();
        em.persist(파스타집);

        em.flush();
        em.clear();

        Store found = em.find(Store.class, 파스타집.getId());

        // Entity 직접 반환 대신 DTO로 변환
        StoreDTO storeDTO = StoreDTO.from(found);
        System.out.println("storeDTO = " + storeDTO);

        /**
         * Menu 엔티티 합류 후:
         * StoreDTO에 List<MenuDTO> menus 추가 예정
         * MenuDTO에는 store 참조가 없으므로 순환 참조 차단됨
         *
         * Store → MenuDTO (store 필드 없음) → 참조 끊김 → 순환 참조 없음
         */
    }
}
