package com.sparta.newspeed.security.oauth2;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sparta.newspeed.domain.auth.dto.TokenResponseDto;
import com.sparta.newspeed.domain.auth.service.AuthService;
import com.sparta.newspeed.common.util.JwtUtil;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;

import java.io.IOException;

@RequiredArgsConstructor
public class OAuth2SuccessHandler extends SimpleUrlAuthenticationSuccessHandler {
    private final JwtUtil jwtUtil;
    private final AuthService authService;

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException, ServletException {
        OAuth2CustomUser oauthUser = (OAuth2CustomUser) authentication.getPrincipal();
        TokenResponseDto tokenDto = jwtUtil.createToken(oauthUser.getUser().getUserId(), oauthUser.getUser().getRole());
        authService.updateRefreshToken(oauthUser.getUser(), tokenDto.getRefreshToken());

        // 헤더에 넣기
//        response.setHeader("Authorization", tokenDto.getAccessToken());
//        response.setHeader("Authorization-refresh", tokenDto.getRefreshToken());

        // body에 넣기
        String token = new ObjectMapper().writeValueAsString(tokenDto);
        response.setContentType("application/json");
        response.getWriter().write(token);
    }
}
