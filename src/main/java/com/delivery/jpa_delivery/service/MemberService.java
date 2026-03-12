package com.delivery.jpa_delivery.service;

import com.delivery.jpa_delivery.entity.Member;
import com.delivery.jpa_delivery.entity.Orders;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.EntityTransaction;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class MemberService {

    private final EntityManagerFactory emf;
    private final BCryptPasswordEncoder passwordEncoder

    /** 1. 회원 단건 조회 (ID 기반) */
    public Member findOne(Long memberId) {
        EntityManager em = emf.createEntityManager(); //
        try {
            Member member = em.find(Member.class, memberId); //
            if (member == null) {
                throw new IllegalArgumentException("존재하지 않는 회원입니다."); //
            }
            return member;
        } finally {
            em.close();
        }
    }

    /** 2. 모든 회원 조회 */
    public List<Member> findAll() {
        EntityManager em = emf.createEntityManager(); //
        try {
            return em.createQuery("select m from Member m", Member.class) //
                    .getResultList(); //
        } finally {
            em.close(); //
        }
    }

    /** 3. 특정 회원의 주문 목록 조회 (성능 최적화 버전) */
    public List<Orders> getMemberOrders(Long memberId) {
        EntityManager em = emf.createEntityManager();
        try {
            List<Member> members = em.createQuery(
                            "select m from Member m join fetch m.orders where m.id = :memberId", Member.class) //
                    .setParameter("memberId", memberId) //
                    .getResultList(); //

            if (members.isEmpty()) {
                throw new IllegalArgumentException("해당 회원이 없습니다."); //
            }

            return members.get(0).getOrders(); //
        } finally {
            em.close();
        }
    }

    /** 4. 회원 가입 (수동 트랜잭션 제어) */
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
            if (tx.isActive()) {
                tx.rollback();
            }
            throw e;
        } finally {
            em.close();
        }
    }
}