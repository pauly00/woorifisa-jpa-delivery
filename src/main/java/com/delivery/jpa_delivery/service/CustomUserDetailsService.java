package com.delivery.jpa_delivery.service;

import com.delivery.jpa_delivery.entity.Member;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CustomUserDetailsService implements UserDetailsService {

    private final MemberService memberService;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        Member member = memberService.findByUsername(username);

        if (member == null) {
            throw new UsernameNotFoundException("존재하지 않는 사용자입니다: " + username);
        }

        // 시큐리티 전용 UserDetails 객체로 변환하여 반환
        return User.builder()
                .username(member.getUsername())
                .password(member.getPassword()) // DB에 저장된 암호화된 비번
                .roles(member.getRole().name().replace("ROLE_", "")) // hasRole("USER")와 매칭
                .build();
    }
}