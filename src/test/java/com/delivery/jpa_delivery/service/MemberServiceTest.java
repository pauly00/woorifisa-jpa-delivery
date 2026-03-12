package com.delivery.jpa_delivery.service;

import com.delivery.jpa_delivery.entity.Member;
import com.delivery.jpa_delivery.entity.Orders;
import com.delivery.jpa_delivery.entity.OrderStatus;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
class MemberServiceTest {

    @Autowired
    private MemberService memberService;

    @Autowired
    private EntityManagerFactory emf;

    @Test
    @DisplayName("회원 가입 테스트 (persist 활용)")
    void joinTest() {
        // given
        Member member = Member.builder()
                .username("미뇽")
                .address("대구")
                .build();

        // when
        Long savedId = memberService.join(member);

        // then
        Member findMember = memberService.findOne(savedId);
        assertThat(findMember.getUsername()).isEqualTo("미뇽");
        assertThat(findMember.getAddress()).isEqualTo("대구");
    }

    @Test
    @DisplayName("전체 회원 조회 테스트 (Criteria API 활용)")
    void findAllTest() {
        EntityManager em = emf.createEntityManager();
        em.getTransaction().begin();

        em.persist(Member.builder().username("UserA").build());
        em.persist(Member.builder().username("UserB").build());

        em.getTransaction().commit();
        em.close();

        // when
        List<Member> members = memberService.findAll();

        // then
        assertThat(members.size()).isGreaterThanOrEqualTo(2);
        assertThat(members).extracting("name").contains("UserA", "UserB");
    }

    @Test
    @DisplayName("회원의 주문 목록 조회 테스트 (EntityGraph 활용)")
    void getMemberOrdersTest() {
        // given (회원과 주문 데이터 준비)
        EntityManager em = emf.createEntityManager();
        em.getTransaction().begin();

        Member member = Member.builder().username("주문자").build();
        em.persist(member);

        // 연관관계 편의 메서드를 통해 주문 생성
        Orders order1 = new Orders();
        order1.setMember(member);
        order1.setOrderDate(LocalDateTime.now());
        order1.setStatus(OrderStatus.ORDER);
        em.persist(order1);

        Orders order2 = new Orders();
        order2.setMember(member);
        order2.setOrderDate(LocalDateTime.now());
        order2.setStatus(OrderStatus.ORDER);
        em.persist(order2);

        em.getTransaction().commit();
        Long memberId = member.getId();
        em.close();

        // when
        List<Orders> orders = memberService.getMemberOrders(memberId);

        // then
        assertThat(orders).hasSize(2);
        assertThat(orders.get(0).getMember().getUsername()).isEqualTo("주문자");
    }
}