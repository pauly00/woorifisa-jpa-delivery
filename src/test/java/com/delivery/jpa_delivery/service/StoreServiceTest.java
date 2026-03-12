package com.delivery.jpa_delivery.service;

import com.delivery.jpa_delivery.dto.StoreDTO;
import com.delivery.jpa_delivery.entity.Menu;
import com.delivery.jpa_delivery.entity.Store;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.EntityTransaction;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
class StoreServiceTest {

    @Autowired
    StoreService storeService;

    @Autowired
    EntityManagerFactory emf;

    @Test
    @DisplayName("가게를 저장하면 DB에 정상적으로 등록된다")
    void testSave() {
        Store 맛나분식 = Store.builder()
                .name("맛나분식")
                .address("서울 강남구")
                .build();

        Store saved = storeService.save(맛나분식);

        assertThat(saved.getId()).isNotNull();
        assertThat(saved.getName()).isEqualTo("맛나분식");
        System.out.println("저장된 가게: " + saved);
    }

    @Test
    @DisplayName("저장된 가게를 ID로 단건 조회할 수 있다")
    void testFind() {
        Store 피자집 = Store.builder()
                .name("피자집")
                .address("서울 마포구")
                .build();
        storeService.save(피자집);

        Store found = storeService.findById(피자집.getId());

        assertThat(found).isNotNull();
        assertThat(found.getName()).isEqualTo("피자집");
        System.out.println("조회된 가게: " + found);
    }

    @Test
    @DisplayName("저장된 전체 가게 목록을 JPQL로 조회할 수 있다")
    void testFindAll() {
        storeService.save(Store.builder().name("맛나분식").address("서울 강남구").build());
        storeService.save(Store.builder().name("피자헛").address("서울 마포구").build());
        storeService.save(Store.builder().name("초밥집").address("서울 종로구").build());

        List<Store> stores = storeService.findAll();

        assertThat(stores).isNotEmpty();
        System.out.println("전체 가게 수: " + stores.size());
        stores.forEach(s -> System.out.println("  - " + s));
    }

    @Test
    @DisplayName("가게를 삭제하면 소속 메뉴도 함께 삭제된다")
    void testDelete() {
        EntityManager em = emf.createEntityManager();
        EntityTransaction tx = em.getTransaction();

        // 가게와 메뉴를 함께 저장
        Long storeId;
        try {
            tx.begin();

            Store 국밥집 = Store.builder()
                    .name("국밥집")
                    .address("서울 종로구")
                    .build();
            em.persist(국밥집);

            // Menu.confirmStore()가 menu.store 설정 + 가게의 menus 리스트에 추가를 동시에 처리
            Menu 뚝배기국밥 = Menu.builder().name("뚝배기국밥").price(9000).build();
            뚝배기국밥.setStore(국밥집);
            em.persist(뚝배기국밥);

            tx.commit();
            storeId = 국밥집.getId();
        } catch (Exception e) {
            tx.rollback();
            throw e;
        } finally {
            em.close();
        }

        // CascadeType.ALL 설정으로 가게 삭제 시 소속 메뉴도 자동 삭제됨
        storeService.delete(storeId);

        Store deleted = storeService.findById(storeId);
        assertThat(deleted).isNull();
        System.out.println("삭제 후 조회 결과: " + deleted); // null
    }

    @Test
    @DisplayName("Store 엔티티를 StoreDTO로 변환하면 순환 참조가 발생하지 않는다")
    void testDtoConversion() {
        Store 파스타집 = Store.builder()
                .name("파스타집")
                .address("서울 용산구")
                .build();
        storeService.save(파스타집);

        Store found = storeService.findById(파스타집.getId());

        // Entity를 직접 반환하지 않고 DTO로 변환하여 순환 참조를 차단
        StoreDTO storeDTO = StoreDTO.from(found);

        assertThat(storeDTO.getName()).isEqualTo("파스타집");
        System.out.println("storeDTO = " + storeDTO);

        /**
         * Store → StoreDTO 변환 후에는
         * StoreDTO에 store 참조가 없으므로 순환 참조가 발생하지 않는다
         */
    }

    @Test
    @DisplayName("Menu의 confirmStore()를 호출하면 가게와 메뉴의 양방향 연관관계가 모두 설정된다")
    void testMenuStoreRelationship() {
        EntityManager em = emf.createEntityManager();
        EntityTransaction tx = em.getTransaction();

        Long storeId;
        try {
            tx.begin();

            Store 치킨집 = Store.builder()
                    .name("치킨집")
                    .address("서울 서초구")
                    .build();
            em.persist(치킨집);

            // Menu가 연관관계의 주인(store_id FK 보유)
            // confirmStore()가 menu.store = 가게 + 가게.menus.add(menu) 양쪽 연관관계를 동시에 설정
            Menu 후라이드 = Menu.builder().name("후라이드").price(18000).build();
            Menu 양념치킨 = Menu.builder().name("양념치킨").price(19000).build();

            후라이드.setStore(치킨집);
            양념치킨.setStore(치킨집);

            em.persist(후라이드);
            em.persist(양념치킨);

            tx.commit();
            storeId = 치킨집.getId();
        } catch (Exception e) {
            tx.rollback();
            throw e;
        } finally {
            em.close();
        }

        // 가게 조회 후 소속 메뉴 목록 확인 (양방향 연관관계 검증)
        Store found = storeService.findById(storeId);
        assertThat(found.getMenus()).hasSize(2);
        System.out.println("가게 이름: " + found.getName());
        System.out.println("소속 메뉴 수: " + found.getMenus().size());
        found.getMenus().forEach(m ->
                System.out.println("  - " + m.getName() + " : " + m.getPrice() + "원"));

        /**
         * confirmStore()는 연관관계 편의 메서드로,
         * 연관관계의 주인(Menu) 쪽만 설정해도 반대편(Store.menus)까지 함께 적용됨
         */
    }
}
