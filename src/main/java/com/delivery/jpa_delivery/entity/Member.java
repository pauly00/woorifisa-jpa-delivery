package com.delivery.jpa_delivery.entity;

import jakarta.persistence.*;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@Setter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
public class Member {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "member_id")
    private Long id;

    @Column(nullable = false, unique = true, length = 50, updatable = false)
    private String username;

    @Column(nullable = false)
    private String password; // BCrypt로 암호화된 비밀번호가 저장될 곳
    private String address;

    @Enumerated(EnumType.STRING)
    private Role role;

    // 회원이 주문한 내역들 (1:N 양방향 매핑)
    // mappedBy: Orders 엔티티에 있는 'member' 필드에 의해 매핑됨을 의미
    @Builder.Default
    @OneToMany(mappedBy = "member", cascade = CascadeType.ALL)
    private List<Orders> orders = new ArrayList<>();

    // --- 연관관계 편의 메서드 ---
    // 주문이 생성될 때 Member 객체에도 주문 정보를 넣어주는 편의 메서드.
    public void addOrder(Orders order) {
        this.orders.add(order);
        if (order.getMember() != this) {
            order.setMember(this);
        }
    }

    // 비밀번호 업데이트 로직
    public void updatePassword(String encodedPassword) {
        this.password = encodedPassword;
    }

    // 주소 변경 로직
    public void updateAddress(String newAddress) {
        if (newAddress != null && !newAddress.isBlank()) {
            this.address = newAddress;
        }
    }
}