package com.delivery.jpa_delivery.service;

import com.delivery.jpa_delivery.entity.Menu;
import com.delivery.jpa_delivery.entity.Store;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
class MenuServiceTest {

    @Autowired
    private MenuService menuService;

    @Autowired
    private EntityManagerFactory emf;

    @Test
    @DisplayName("메뉴 저장 테스트")
    void saveMenuTest() {

        EntityManager em = emf.createEntityManager();
        em.getTransaction().begin();

        Store store = Store.builder().name("치킨집").build();
        em.persist(store);

        em.getTransaction().commit();
        Long storeId = store.getId();
        em.close();

        // when
        Menu menu = Menu.builder().name("황금올리브").price(20000).build();
        Long savedMenuId = menuService.save(menu, storeId);

        // then
        Menu findMenu = menuService.findOne(savedMenuId);
        assertThat(findMenu.getName()).isEqualTo("황금올리브");
        assertThat(findMenu.getStore().getName()).isEqualTo("치킨집");
    }

    @Test
    @DisplayName("메뉴 수정 테스트")
    void updateMenuTest() {

        EntityManager em = emf.createEntityManager();
        em.getTransaction().begin();
        Store store = Store.builder().name("분식집").build();
        em.persist(store);
        Menu menu = Menu.builder().name("기본김밥").price(3000).build();
        menu.setStore(store);
        em.persist(menu);
        em.getTransaction().commit();
        Long menuId = menu.getId();
        em.close();

        // 가격 및 이름 수정
        menuService.updateMenu(menuId, "참치김밥", 4500);

        // then
        Menu updatedMenu = menuService.findOne(menuId);
        assertThat(updatedMenu.getName()).isEqualTo("참치김밥");
        assertThat(updatedMenu.getPrice()).isEqualTo(4500);
    }

    @Test
    @DisplayName("특정 가게의 모든 메뉴 조회 테스트")
    void findByStoreTest() {
        EntityManager em = emf.createEntityManager();
        em.getTransaction().begin();
        Store store = Store.builder().name("중국집").build();
        em.persist(store);

        Menu m1 = Menu.builder().name("짜장면").price(7000).build();
        m1.setStore(store);
        Menu m2 = Menu.builder().name("짬뽕").price(8000).build();
        m2.setStore(store);

        em.persist(m1);
        em.persist(m2);
        em.getTransaction().commit();
        Long storeId = store.getId();
        em.close();

        // when
        List<Menu> menus = menuService.findByStore(storeId);

        // then
        assertThat(menus).hasSize(2);
        assertThat(menus).extracting("name").containsExactlyInAnyOrder("짜장면", "짬뽕");
    }
}