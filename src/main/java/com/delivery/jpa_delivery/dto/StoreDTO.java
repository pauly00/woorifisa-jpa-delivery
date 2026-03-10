package com.delivery.jpa_delivery.dto;

import com.delivery.jpa_delivery.entity.Store;
import java.util.List;
import lombok.Getter;

@Getter
public class StoreDTO {

    private Long id;
    private String name;
    private String address;

    // menus 필드는 Menu 엔티티(4번 팀원) 합류 후 추가 예정
     private List<MenuDTO> menus;

    private StoreDTO(Long id, String name, String address) {
        this.id = id;
        this.name = name;
        this.address = address;
    }

    /**
     * Store 엔티티를 인수로 받아 StoreDTO로 변환하는 정적 팩토리 메서드
     * "Store 엔티티로부터(from) StoreDTO를 생성(변환)"
     */
    public static StoreDTO from(Store store) {
        return new StoreDTO(
                store.getId(),
                store.getName(),
                store.getAddress()
        );
    }

    @Override
    public String toString() {
        return "StoreDTO{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", address='" + address + '\'' +
                '}';
    }
}
