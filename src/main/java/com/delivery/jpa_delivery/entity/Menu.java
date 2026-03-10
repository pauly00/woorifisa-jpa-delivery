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
    private Long id;

    private String name;
    private int price;

    @ManyToOne
    private Store store;

    public void confirmStore(Store store) {
        this.store = store;
        store.getMenus().add(this);
    }

    @OneToMany(mappedBy = "menu") // Orders 엔티티에 있는 'menu' 필드명과 일치해야
    private List<Orders> orders = new ArrayList<>();
}
