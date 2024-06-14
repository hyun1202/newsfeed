package com.sparta.newspeed.domain.auth.social.module;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;

@Component
public class KakaoModule implements OAuthModule{
    private final String KAKAO_CLIENT_ID;
    private final String KAKAO_CLIENT_SECRET;
    private static final String SCOPE = "profile_nickname%20profile_image%20account_email";
    private final String REDIRECT_URL = "http%3A%2F%2Flocalhost%3A8081%2Fapi%2Fsocial%2Fcallback%2Fkakao";

    public KakaoModule(@Value("${spring.security.oauth2.client.registration.kakao.client-id}") String kakaoClientId,
                       @Value("${spring.security.oauth2.client.registration.kakao.client-secret}") String kakaoClientSecret) {
        KAKAO_CLIENT_ID = kakaoClientId;
        KAKAO_CLIENT_SECRET = kakaoClientSecret;
    }

    @Override
    public String getClientId() {
        return this.KAKAO_CLIENT_ID;
    }

    @Override
    public String getClientSecret() {
        return this.KAKAO_CLIENT_SECRET;
    }

    @Override
    public String getSocialLoginUri() {
        return UriComponentsBuilder
                .fromUriString("https://kauth.kakao.com/oauth/authorize")
                .queryParam("response_type", "code")
                .queryParam("client_id", KAKAO_CLIENT_ID)
                .queryParam("scope", SCOPE)
                .queryParam("redirect_uri", REDIRECT_URL)
                .build()
                .toUriString();
    }

    @Override
    public URI getRefreshTokenUri(String refreshToken) {
        return UriComponentsBuilder
                .fromUriString("https://kauth.kakao.com/oauth/token")
                .queryParam("grant_type", "refresh_token")
                .queryParam("client_id", KAKAO_CLIENT_ID)
                .queryParam("client_secret", KAKAO_CLIENT_SECRET)
                .queryParam("refresh_token", refreshToken)
                .build()
                .toUri();
    }

    @Override
    public URI getSocialUserUri(String accessToken) {
        return UriComponentsBuilder
                .fromUriString("https://kapi.kakao.com/v2/user/me")
                .build()
                .toUri();
    }


    @Override
    public URI getAccessTokenUri(String code, String state) {
        return UriComponentsBuilder
                .fromUriString("https://kauth.kakao.com/oauth/token")
                .queryParam("grant_type", "authorization_code")
                .queryParam("client_id", KAKAO_CLIENT_ID)
                .queryParam("client_secret", KAKAO_CLIENT_SECRET)
                .queryParam("code", code)
                .queryParam("state", state)
                .build()
                .toUri();
    }
}
