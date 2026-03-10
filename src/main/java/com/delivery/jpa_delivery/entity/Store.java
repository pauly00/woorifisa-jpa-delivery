package com.delivery.jpa_delivery.entity;

import jakarta.persistence.*;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "store")
public class Store {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;

    private String address;

    /**
     * Store(1) : Menu(N)
     * Menu 테이블이 store_id(FK)를 가지므로 Menu.store 가 연관관계의 주인
     * mappedBy - 반대쪽 매핑(Menu 클래스의 store 필드명)을 지정
     * CascadeType.ALL - 가게 삭제 시 메뉴도 함께 삭제
     * (Menu 엔티티는 4번 팀원 담당)
     */
     @OneToMany(mappedBy = "store", cascade = CascadeType.ALL, orphanRemoval = true)
     @Builder.Default
     private List<Menu> menus = new ArrayList<>();

    @Override
    public String toString() {
        return "Store{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", address='" + address + '\'' +
                '}';
    }
}
