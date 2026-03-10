package com.delivery.jpa_delivery.service;

import com.delivery.jpa_delivery.dto.OrderDTO;
import com.delivery.jpa_delivery.entity.*;
import jakarta.persistence.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class OrderService {

    private final EntityManagerFactory emf;

    // 1. 주문 생성
    public Long createOrder(Long memberId, Long menuId) {
        EntityManager em = emf.createEntityManager();
        EntityTransaction tx = em.getTransaction();
        Long orderId = null;

        try {
            tx.begin();

            Member member = em.find(Member.class, memberId);
            Menu menu = em.find(Menu.class, menuId);

            if (member == null || menu == null) throw new RuntimeException("데이터 없음");

            Orders order = new Orders();
            order.setMember(member); // 연관관계 편의 메서드 작동
            order.setMenu(menu);
            order.setOrderDate(LocalDateTime.now());
            order.setStatus(OrderStatus.ORDER);

            em.persist(order); // 영속화

            tx.commit(); // 커밋 시점에 INSERT SQL 전송
            orderId = order.getId();
        } catch (Exception e) {
            if (tx.isActive()) tx.rollback();
            throw e;
        } finally {
            em.close();
        }
        return orderId;
    }

    // 2. 주문 단건 조회 (DTO 변환 포함)
    public OrderDTO getOrder(Long orderId) {
        EntityManager em = emf.createEntityManager();
        try {
            Orders order = em.find(Orders.class, orderId);
            if (order == null) return null;

            // DTO로 변환하여 반환
            return OrderDTO.from(order);
        } finally {
            em.close();
        }
    }

    // 3. 주문 취소
    public void cancelOrder(Long orderId) {
        EntityManager em = emf.createEntityManager();
        EntityTransaction tx = em.getTransaction();
        try {
            tx.begin();
            Orders order = em.find(Orders.class, orderId);
            if (order != null) {
                order.setStatus(OrderStatus.CANCEL);
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