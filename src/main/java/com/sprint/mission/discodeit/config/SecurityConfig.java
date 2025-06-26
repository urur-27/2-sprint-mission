package com.sprint.mission.discodeit.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sprint.mission.discodeit.mapper.UserMapper;
import com.sprint.mission.discodeit.security.JsonLogoutFilter;
import com.sprint.mission.discodeit.security.JsonUsernamePasswordAuthenticationFilter;
import com.sprint.mission.discodeit.security.LoginFailureHandler;
import com.sprint.mission.discodeit.security.LoginSuccessHandler;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;import org.springframework.context.annotation.Configuration;
import org.springframework.security.access.hierarchicalroles.RoleHierarchy;
import org.springframework.security.access.hierarchicalroles.RoleHierarchyImpl;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.ProviderManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.context.HttpSessionSecurityContextRepository;
import org.springframework.security.web.csrf.CookieCsrfTokenRepository;
import org.springframework.security.web.csrf.CsrfTokenRepository;

@Slf4j
@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    private final UserDetailsService userDetailsService;

    @Bean
    public JsonLogoutFilter jsonLogoutFilter() {
        return new JsonLogoutFilter();
    }

    @Bean
    public JsonUsernamePasswordAuthenticationFilter jsonLoginFilter(
        ObjectMapper objectMapper,
        UserMapper userMapper,
        AuthenticationManager authenticationManager
    ) {
        // 로그인 요청 파싱해서 커스텀 필터 객체 생성
        JsonUsernamePasswordAuthenticationFilter loginFilter =
            new JsonUsernamePasswordAuthenticationFilter(objectMapper);
        // 인증을 위임할 매니저 생성
        loginFilter.setAuthenticationManager(authenticationManager);
        // 인증 성공시 SecurityContext를 저장할 장소 지정
        loginFilter.setSecurityContextRepository(new HttpSessionSecurityContextRepository());
        // 인증 성공/실패 핸들러 지정
        loginFilter.setAuthenticationSuccessHandler(new LoginSuccessHandler(objectMapper, userMapper));
        loginFilter.setAuthenticationFailureHandler(new LoginFailureHandler(objectMapper));
        // 필터 처리 경로 설정
        loginFilter.setFilterProcessesUrl("/api/auth/login");
        return loginFilter;
    }

    // 권한 계층 설정
    @Bean
    public RoleHierarchy roleHierarchy() {
        RoleHierarchyImpl hierarchy = new RoleHierarchyImpl();
        hierarchy.setHierarchy("ROLE_ADMIN > ROLE_CHANNEL_MANAGER \nROLE_CHANNEL_MANAGER > ROLE_USER");
        return hierarchy;
    }


    /**
     * Spring Security 필터 체인 설정
     */
    @Bean
    public SecurityFilterChain filterChain(
        HttpSecurity http,
        JsonLogoutFilter logoutFilter,
        JsonUsernamePasswordAuthenticationFilter loginFilter) throws Exception {

        http
            .csrf(csrf -> csrf
                .csrfTokenRepository(csrfTokenRepository())
                .ignoringRequestMatchers(
                    "/api/auth/login",
                    "/api/users",
                    "/api/auth/csrf-token",
                    "/api/auth/logout"
                )
            )
            .authorizeHttpRequests(auth -> auth
                .requestMatchers(
                    "/api/auth/login",
                    "/api/auth/csrf-token",
                    "/api/users"
                ).permitAll()
                .requestMatchers("/api/**").authenticated()
                .anyRequest().permitAll()
            )
            .formLogin(AbstractHttpConfigurer::disable)
            .httpBasic(AbstractHttpConfigurer::disable)
            .logout(AbstractHttpConfigurer::disable)
            .addFilterAt(loginFilter, UsernamePasswordAuthenticationFilter.class)
            .addFilterBefore(logoutFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }

    // csrf 토큰
    @Bean
    public CsrfTokenRepository csrfTokenRepository() {
        CookieCsrfTokenRepository repository = CookieCsrfTokenRepository.withHttpOnlyFalse();
        repository.setCookieName("CSRF-TOKEN");
        repository.setHeaderName("X-CSRF-TOKEN");
        return repository;
    }

    // 비밀번호 인코더
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    // 인증 매니저
    @Bean
    public AuthenticationManager authenticationManager(){
        DaoAuthenticationProvider provider = new DaoAuthenticationProvider();
        provider.setUserDetailsService(userDetailsService);
        provider.setPasswordEncoder(passwordEncoder());
        return new ProviderManager(provider);
    }

}
