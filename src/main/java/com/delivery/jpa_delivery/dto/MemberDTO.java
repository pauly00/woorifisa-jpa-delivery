package com.delivery.jpa_delivery.dto;

import lombok.*;

@Getter @Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MemberDTO {
    private Long id;
    private String username;
    private String password;
    private String address;
    private String role;
}