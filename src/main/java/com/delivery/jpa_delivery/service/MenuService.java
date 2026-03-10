package com.delivery.jpa_delivery.service;

import com.delivery.jpa_delivery.entity.Menu;
import com.delivery.jpa_delivery.entity.Store;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.EntityTransaction;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class MenuService {
    
    private final EntityManagerFactory emf;

    // 1. 메뉴 저장
    public Long save(Menu menu, Long storeId) {
        EntityManager em = emf.createEntityManager();
        EntityTransaction tx = em.getTransaction();

        try {
            tx.begin();

            Store store = em.find(Store.class, storeId);
            if (store == null) {
                throw new IllegalArgumentException("존재하지 않는 가게입니다. ID: " + storeId);
            }

            menu.setStore(store);
            em.persist(menu);

            tx.commit();
            return menu.getId();
        } catch (Exception e) {
            tx.rollback();
            throw e;
        } finally {
            em.close();
        }
    }

    // 2. 메뉴 단건 조회
    public Menu findOne(Long menuId) {
        EntityManager em = emf.createEntityManager();
        try {
            Menu menu = em.find(Menu.class, menuId);
            if (menu == null) {
                throw new IllegalArgumentException("존재하지 않는 메뉴입니다.");
            }
            return menu;
        } finally {
            em.close();
        }
    }

    // 3. 전체 메뉴 조회
    public List<Menu> findAll() {
        EntityManager em = emf.createEntityManager();
        try {
            return em.createQuery("select m from Menu m", Menu.class)
                    .getResultList();
        } finally {
            em.close();
        }
    }

    // 4. 특정 가게의 메뉴 목록 조회
    public List<Menu> findByStore(Long storeId) {
        EntityManager em = emf.createEntityManager();
        try {
            return em.createQuery(
                            "select m from Menu m where m.store.id = :storeId", Menu.class)
                    .setParameter("storeId", storeId)
                    .getResultList();
        } finally {
            em.close();
        }
    }

    // 메뉴 가격 수정
    public void updateMenu(Long menuId, String name, int price) {
        EntityManager em = emf.createEntityManager();
        EntityTransaction tx = em.getTransaction();

        try {
            tx.begin();

            Menu menu = em.find(Menu.class, menuId);
            if (menu == null) {
                throw new IllegalArgumentException("수정하려는 메뉴가 없습니다.");
            }

            // 변경 감지
            menu.setName(name);
            menu.setPrice(price);

            tx.commit();
        } catch (Exception e) {
            tx.rollback();
            throw e;
        } finally {
            em.close();
        }
    }
}