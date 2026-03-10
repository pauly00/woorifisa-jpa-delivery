package com.delivery.jpa_delivery.service;

import com.delivery.jpa_delivery.entity.Store;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.EntityTransaction;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class StoreService {

    private final EntityManagerFactory emf;

    // 가게 등록
    public Store save(Store store) {
        EntityManager em = emf.createEntityManager();
        EntityTransaction tx = em.getTransaction();
        try {
            tx.begin();
            em.persist(store);
            tx.commit();
        } catch (Exception e) {
            if (tx.isActive()) tx.rollback();
            throw e;
        } finally {
            em.close();
        }
        return store;
    }

    // 가게 단건 조회
    public Store findById(Long id) {
        EntityManager em = emf.createEntityManager();
        try {
            return em.find(Store.class, id);
        } finally {
            em.close();
        }
    }

    // 전체 가게 목록 조회
    public List<Store> findAll() {
        EntityManager em = emf.createEntityManager();
        try {
            return em.createQuery("SELECT s FROM Store s", Store.class)
                    .getResultList();
        } finally {
            em.close();
        }
    }

    // 가게 삭제
    public void delete(Long id) {
        EntityManager em = emf.createEntityManager();
        EntityTransaction tx = em.getTransaction();
        try {
            tx.begin();
            Store store = em.find(Store.class, id);
            if (store != null) {
                em.remove(store);
            }
            tx.commit();
        } catch (Exception e) {
            if (tx.isActive()) tx.rollback();
            throw e;
        } finally {
            em.close();
        }
    }
}
