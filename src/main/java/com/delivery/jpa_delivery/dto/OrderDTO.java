package com.delivery.jpa_delivery.dto;

import com.delivery.jpa_delivery.entity.Orders;
import com.delivery.jpa_delivery.entity.OrderStatus;
import lombok.AllArgsConstructor;
import lombok.Data;
import java.time.LocalDateTime;

@Data
@AllArgsConstructor
public class OrderDTO {
    private Long orderId;
    private String memberName;
    private String menuName;
    private int price;
    private LocalDateTime orderDate;
    private OrderStatus status;

    // 엔티티를 DTO로 변환하는 정적 팩토리 메서드
    public static OrderDTO from(Orders order) {
        return new OrderDTO(
                order.getId(),
                order.getMember().getUsername(),
                order.getMenu().getName(),
                order.getMenu().getPrice(),
                order.getOrderDate(),
                order.getStatus()
        );
    }
}