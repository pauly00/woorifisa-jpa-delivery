package com.delivery.jpa_delivery.entity;

import jakarta.persistence.*;
import java.util.ArrayList;
import java.util.List;
import lombok.*;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Menu {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "menu_id")
    private Long id;

    private String name;
    private int price;

    @ManyToOne(fetch = FetchType.LAZY) // 가게 정보를 실제로 쓸 때만 가져와라
    @JoinColumn(name = "store_id")
    private Store store;

    // 양방향 연관관계 편의 메소드
    public void setStore(Store store) {
        this.store = store;
        store.getMenus().add(this);
    }

    @Builder.Default
    @OneToMany(mappedBy = "menu")
    private List<Orders> orders = new ArrayList<>();
}
