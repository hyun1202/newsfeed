package com.sparta.newspeed.domain.auth.social.module;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;

@Component
public class NaverModule implements OAuthModule{
    private final String NAVER_CLIENT_ID;
    private final String NAVER_CLIENT_SECRET;
    private static final String SCOPE = "name%20email%20profile_image";
    private static final String REDIRECT_URL = "http%3A%2F%2Flocalhost%3A8081%2Fapi%2Fsocial%2Fcallback%2Fnaver";

    public NaverModule(@Value("${spring.security.oauth2.client.registration.naver.client-id}") String naverClientId,
                       @Value("${spring.security.oauth2.client.registration.naver.client-secret}") String naverClientSecret) {
        NAVER_CLIENT_ID = naverClientId;
        NAVER_CLIENT_SECRET = naverClientSecret;
    }

    @Override
    public String getClientId() {
        return this.NAVER_CLIENT_ID;
    }

    @Override
    public String getClientSecret() {
        return this.NAVER_CLIENT_SECRET;
    }

    @Override
    public String getSocialLoginUri() {
        return UriComponentsBuilder
                .fromUriString("https://nid.naver.com/oauth2.0/authorize")
                .queryParam("response_type", "code")
                .queryParam("client_id", NAVER_CLIENT_ID)
                .queryParam("scope", SCOPE)
                .queryParam("redirect_uri", REDIRECT_URL)
                .build()
                .toUriString();
    }

    @Override
    public URI getRefreshTokenUri(String refreshToken) {
        return UriComponentsBuilder
                .fromUriString("https://nid.naver.com/oauth2.0/token")
                .queryParam("grant_type", "refresh_token")
                .queryParam("client_id", NAVER_CLIENT_ID)
                .queryParam("client_secret", NAVER_CLIENT_SECRET)
                .queryParam("refresh_token", refreshToken)
                .build()
                .toUri();
    }

    @Override
    public URI getSocialUserUri(String accessToken) {
        return UriComponentsBuilder
                .fromUriString("https://openapi.naver.com/v1/nid/me")
                .build()
                .toUri();
    }


    @Override
    public URI getAccessTokenUri(String code, String state) {
        return UriComponentsBuilder
                .fromUriString("https://nid.naver.com/oauth2.0/token")
                .queryParam("grant_type", "authorization_code")
                .queryParam("client_id", NAVER_CLIENT_ID)
                .queryParam("client_secret", NAVER_CLIENT_SECRET)
                .queryParam("code", code)
                .queryParam("state", state)
                .build()
                .toUri();
    }
}
