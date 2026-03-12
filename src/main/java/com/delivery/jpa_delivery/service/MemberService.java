package com.delivery.jpa_delivery.service;

import com.delivery.jpa_delivery.entity.Member;
import com.delivery.jpa_delivery.entity.Orders;
import jakarta.persistence.EntityGraph;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.EntityTransaction;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Root;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class MemberService {

    private final EntityManagerFactory emf;
    private final BCryptPasswordEncoder passwordEncoder;

    /** 1. 회원 단건 조회 (ID 기반) */
    public Member findOne(Long memberId) {
        EntityManager em = emf.createEntityManager();
        try {
            Member member = em.find(Member.class, memberId);
            if (member == null) {
                throw new IllegalArgumentException("존재하지 않는 회원입니다.");
            }
            return member;
        } finally {
            em.close();
        }
    }

    /** 2. 모든 회원 조회 (Criteria API 활용 - 문자열 쿼리 제거) */
    public List<Member> findAll() {
        EntityManager em = emf.createEntityManager();
        try {
            CriteriaBuilder cb = em.getCriteriaBuilder();
            CriteriaQuery<Member> cq = cb.createQuery(Member.class);
            Root<Member> root = cq.from(Member.class);
            cq.select(root);

            return em.createQuery(cq).getResultList();
        } finally {
            em.close();
        }
    }

    /** 3. 특정 회원의 주문 목록 조회 (EntityGraph 활용 - Fetch Join 쿼리 제거) */
    public List<Orders> getMemberOrders(Long memberId) {
        EntityManager em = emf.createEntityManager();
        try {
            // 동적 EntityGraph 생성: 연관된 'orders' 정보를 함께 긁어오도록 설정
            EntityGraph<Member> graph = em.createEntityGraph(Member.class);
            graph.addAttributeNodes("orders");

            Map<String, Object> hints = new HashMap<>();
            hints.put("jakarta.persistence.fetchgraph", graph);

            Member member = em.find(Member.class, memberId, hints);

            if (member == null) {
                throw new IllegalArgumentException("해당 회원이 없습니다.");
            }

            return member.getOrders();
        } finally {
            em.close();
        }
    }

    /** 4. 회원 가입 (수동 트랜잭션 및 persist 활용) */
    public Long join(Member member) {
        EntityManager em = emf.createEntityManager();
        EntityTransaction tx = em.getTransaction();
        try {
            tx.begin();

            // 비밀번호 암호화 후 저장
            String encodedPassword = passwordEncoder.encode(member.getPassword());
            member.updatePassword(encodedPassword);

            em.persist(member);

            tx.commit();
            return member.getId();
        } catch (Exception e) {
            if (tx.isActive()) tx.rollback();
            throw e;
        } finally {
            em.close();
        }
    }
}