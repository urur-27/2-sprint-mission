package com.sprint.mission.discodeit.security;

import com.sprint.mission.discodeit.entity.User;
import java.util.Collection;
import java.util.List;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

@RequiredArgsConstructor
public class CustomUserDetails implements UserDetails {

    private final User user;

    public User getUser() {
        return user;
    }

    @Override
    public String getUsername() {
        return user.getUsername();
    }

    @Override
    public String getPassword() {
        return user.getPassword();
    }

    public UUID getUserId() {
        return user.getId();
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return List.of(); // 필요하면 ROLE 추가
    }

    @Override
    public boolean isAccountNonExpired() {
        return true; // 실제 도메인에 따라 변경 가능
    }

    @Override
    public boolean isAccountNonLocked() {
        return true; // 실제 도메인에 따라 변경 가능
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true; // 실제 도메인에 따라 변경 가능
    }

    @Override
    public boolean isEnabled() {
        return true; // 탈퇴/비활성화 여부에 따라 조정 가능
    }
}

