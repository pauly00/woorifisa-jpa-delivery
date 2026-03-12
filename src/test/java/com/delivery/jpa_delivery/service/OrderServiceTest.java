package com.delivery.jpa_delivery.service;

import com.delivery.jpa_delivery.dto.OrderDTO;
import com.delivery.jpa_delivery.entity.*;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.EntityTransaction;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@SpringBootTest
class OrderServiceTest {

    @Autowired
    OrderService orderService;

    @Autowired
    EntityManagerFactory emf;

    Long memberId;
    Long menuId;

    @BeforeEach
    void setUp() {
        EntityManager em = emf.createEntityManager();
        EntityTransaction tx = em.getTransaction();

        try {
            tx.begin();

            // 회원 세팅
            Member member = Member.builder()
                    .username("박지은")
                    .address("서울시 강남구")
                    .build();
            em.persist(member);
            memberId = member.getId();

            // 가게 세팅
            Store store = Store.builder()
                    .name("JPA 치킨")
                    .address("서울시 서초구")
                    .build();
            em.persist(store);

            // 메뉴 세팅
            Menu menu = Menu.builder()
                    .name("황금올리브")
                    .price(20000)
                    .store(store)
                    .build();
            em.persist(menu);
            menuId = menu.getId();

            tx.commit();
        } catch (Exception e) {
            tx.rollback();
        } finally {
            em.close();
        }
    }

    @Test
    @DisplayName("주문 생성 및 단건 조회 테스트 (createOrder & getOrder)")
    void createAndGetOrderTest() {
        Long orderId = orderService.createOrder(memberId, menuId);
        OrderDTO orderDTO = orderService.getOrder(orderId);

        assertThat(orderDTO).isNotNull();
        assertThat(orderDTO.getMemberName()).isEqualTo("박지은");
        assertThat(orderDTO.getMenuName()).isEqualTo("황금올리브");
        assertThat(orderDTO.getPrice()).isEqualTo(20000);
        assertThat(orderDTO.getStatus()).isEqualTo(OrderStatus.ORDER);
    }

    @Test
    @DisplayName("주문 취소 테스트 - 변경 감지(Dirty Checking) 확인")
    void cancelOrderTest() {
        Long orderId = orderService.createOrder(memberId, menuId);
        orderService.cancelOrder(orderId);

        OrderDTO canceledOrder = orderService.getOrder(orderId);
        assertThat(canceledOrder.getStatus()).isEqualTo(OrderStatus.CANCEL);
    }

    @Test
    @DisplayName("예외 테스트 - 존재하지 않는 회원이나 메뉴로 주문 시 실패해야 한다")
    void orderExceptionTest() {
        Long wrongMemberId = 999L;
        Long wrongMenuId = 999L;

        assertThatThrownBy(() -> orderService.createOrder(wrongMemberId, wrongMenuId))
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("데이터 없음");
    }
}