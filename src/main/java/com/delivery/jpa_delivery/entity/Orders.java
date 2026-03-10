package com.delivery.jpa_delivery.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import java.time.LocalDateTime;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Getter
@Setter
@NoArgsConstructor
@Table(name = "orders") // order는 SQL 예약어일 수 있으므로 테이블명 명시
public class Orders {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id") // FK
    private Member member;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "menu_id")   // FK
    private Menu menu;

    private LocalDateTime orderDate;

    @Enumerated(EnumType.STRING)
    private OrderStatus status; // [ORDER, CANCEL] 등

    // == 연관관계 편의 메서드 == //
    public void setMember(Member member) {
        this.member = member;
        member.getOrders().add(this);
    }
}
