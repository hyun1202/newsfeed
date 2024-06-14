package com.sparta.newspeed.config;

import com.sparta.newspeed.domain.auth.service.AuthService;
import com.sparta.newspeed.security.oauth2.CustomOAuth2UserService;
import com.sparta.newspeed.security.oauth2.OAuth2SuccessHandler;
import com.sparta.newspeed.security.AccessDeniedHandler;
import com.sparta.newspeed.security.AuthenticationEntryPoint;
import com.sparta.newspeed.security.filter.JwtAuthorizationFilter;
import com.sparta.newspeed.security.service.UserDetailsServiceImpl;
import com.sparta.newspeed.common.util.JwtUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.autoconfigure.security.servlet.PathRequest;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityCustomizer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity // Spring Security 지원을 가능하게 함
@RequiredArgsConstructor
public class WebSecurityConfig {
    private final JwtUtil jwtUtil;
    private final UserDetailsServiceImpl userDetailsService;
    private final AuthenticationConfiguration authenticationConfiguration;

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration configuration) throws Exception {
        return configuration.getAuthenticationManager();
    }

    @Bean
    public JwtAuthorizationFilter jwtAuthorizationFilter() {
        return new JwtAuthorizationFilter(jwtUtil, userDetailsService);
    }
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http, CustomOAuth2UserService customOAuth2UserService, AuthService authService) throws Exception {
        // CSRF 설정
        http.csrf((csrf) -> csrf.disable());

        // 기본 설정인 Session 방식은 사용하지 않고 JWT 방식을 사용하기 위한 설정
        http.sessionManagement((sessionManagement) ->
                sessionManagement.sessionCreationPolicy(SessionCreationPolicy.STATELESS)
        );

        http.exceptionHandling(exception -> exception
                .authenticationEntryPoint(new AuthenticationEntryPoint())
                .accessDeniedHandler(new AccessDeniedHandler())
        );

        http.authorizeHttpRequests((authorizeHttpRequests) ->
                authorizeHttpRequests
                        .requestMatchers(PathRequest.toStaticResources().atCommonLocations()).permitAll()
                        .requestMatchers("/api/auth/**", "/api/social/**").permitAll()
                        .requestMatchers(HttpMethod.GET, "/api/newsfeeds/**").permitAll()
                        .requestMatchers("/swagger-ui/**", "/v3/api-docs/**", "/api-docs/**", "/swagger-resources/**").permitAll()
                        .anyRequest().authenticated() // 그 외 모든 요청 인증처리
        );

        http.oauth2Login(oauth2 -> oauth2
                .userInfoEndpoint(userInfoEndpointConfig -> userInfoEndpointConfig
                        .userService(customOAuth2UserService))
                .successHandler(new OAuth2SuccessHandler(jwtUtil, authService))

        );
        // 로그인폼 사용 X
        http.formLogin((formLogin) -> formLogin.disable());

        // 필터 관리
        http.addFilterBefore(jwtAuthorizationFilter(), UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }

    @Bean
    @ConditionalOnProperty(name = "spring.h2.console.enabled", havingValue = "true")
    public WebSecurityCustomizer configureH2ConsoleEnable() {
        return web -> web.ignoring().requestMatchers(PathRequest.toH2Console());
    }
}