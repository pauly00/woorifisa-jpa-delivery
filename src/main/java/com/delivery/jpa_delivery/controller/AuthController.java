package com.delivery.jpa_delivery.controller;

import com.delivery.jpa_delivery.dto.MemberDTO;
import com.delivery.jpa_delivery.entity.Member;
import com.delivery.jpa_delivery.entity.Role;
import com.delivery.jpa_delivery.service.MemberService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;

@Controller // 문자열을 반환하면 해당 이름의 HTML 템플릿을 찾아감
@RequiredArgsConstructor
public class AuthController {

    private final MemberService memberService;

    // 메인 페이지 화면 반환
    @GetMapping("/main")
    public String mainPage() {
        return "main"; // resources/templates/main.html 렌더링
    }

    // 회원가입 폼 화면 보여주기 (GET)
    @GetMapping("/join")
    public String joinForm() {
        return "join"; // resources/templates/join.html 렌더링
    }

    // 회원가입 실제 처리 (POST)
    @PostMapping("/join")
    public String join(MemberDTO dto) {
        Member member = Member.builder()
                .username(dto.getUsername())
                .password(dto.getPassword())
                .address(dto.getAddress())
                .role(Role.valueOf(dto.getRole()))
                .build();

        memberService.join(member);

        // 가입 성공 후, 로그인 페이지로 강제 이동(Redirect)
        return "redirect:/login";
    }

    // 커스텀 로그인 페이지 화면 반환 (실제 로그인 POST 처리는 시큐리티가 하므로, 화면을 띄워주는 GET만 만듦)
    @GetMapping("/login")
    public String loginForm() {
        return "login"; // resources/templates/login.html 렌더링
    }

    // 일반 유저 주문 확인 페이지
    @GetMapping("/user/orders")
    public String userOrders() {
        return "user/orders"; // resources/templates/user/orders.html 렌더링
    }

    // 관리자 가게 관리 페이지
    @GetMapping("/admin/stores")
    public String adminStores() {
        return "admin/stores"; // resources/templates/admin/stores.html 렌더링
    }
}