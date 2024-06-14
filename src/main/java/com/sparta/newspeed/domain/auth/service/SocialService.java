package com.sparta.newspeed.domain.auth.service;

import com.sparta.newspeed.domain.auth.dto.TokenResponseDto;
import com.sparta.newspeed.domain.auth.social.OAuthAttributes;
import com.sparta.newspeed.domain.auth.social.OAuthTokenResponseDto;
import com.sparta.newspeed.domain.auth.social.SocialEnum;
import com.sparta.newspeed.domain.auth.social.module.KakaoModule;
import com.sparta.newspeed.domain.auth.social.module.NaverModule;
import com.sparta.newspeed.domain.auth.social.module.OAuthModule;
import com.sparta.newspeed.common.exception.CustomException;
import com.sparta.newspeed.common.exception.ErrorCode;
import com.sparta.newspeed.domain.user.entity.User;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

import java.util.Map;

@Service
public class SocialService {

    private OAuthModule oAuthModule;
    private final NaverModule naverModule;
    private final KakaoModule kakaoModule;
    private final AuthService authService;

    public SocialService(NaverModule naverModule, KakaoModule kakaoModule, AuthService authService) {
        this.naverModule = naverModule;
        this.kakaoModule = kakaoModule;
        this.authService = authService;
    }

    public String getSocialLoginUrl(SocialEnum socialEnum) {
        selectOAuthModule(socialEnum);
        return oAuthModule.getSocialLoginUri();
    }

    public TokenResponseDto socialLogin(SocialEnum socialEnum, String code, String state) {
        selectOAuthModule(socialEnum);
        OAuthTokenResponseDto socialTokenDto = getAccessToken(code, state);
        Map<String, Object> socialUser = getSocialUser(socialTokenDto.getAccess_token());
        User user = saveOrUpdateOAuth2Info(OAuthAttributes.of(socialEnum.name(), socialUser));
        TokenResponseDto tokenDto = authService.createToken(user.getUserId(), user.getRole());
        authService.updateRefreshToken(user, tokenDto.getRefreshToken());

        return TokenResponseDto.builder()
                .accessToken(tokenDto.getAccessToken())
                .refreshToken(tokenDto.getRefreshToken())
                .build();
    }

    private OAuthTokenResponseDto getAccessToken(String code, String state) {
        return RestClient.create().get()
                .uri(oAuthModule.getAccessTokenUri(code, state))
                .retrieve()
                .body(OAuthTokenResponseDto.class);
    }

    private Map getSocialUser(String accessToken) {
        return RestClient.create().get()
                        .uri(oAuthModule.getSocialUserUri(accessToken))
                        .header("Authorization", "Bearer " + accessToken)
                        .retrieve()
                        .onStatus(status -> status.value() != 200, (request, response) -> {
                            throw new CustomException(ErrorCode.SOCIAL_USER_NOT_FOUND);
                        })
                        .body(Map.class);
    }

    private User saveOrUpdateOAuth2Info(OAuthAttributes attributes) {
        return authService.saveOrUpdateOAuth2Info(attributes);
    }

    private void selectOAuthModule(SocialEnum socialEnum) {
        if ("kakao".equals(socialEnum.name())) {
            this.oAuthModule = kakaoModule;
        } else if ("naver".equals(socialEnum.name())) {
            this.oAuthModule = naverModule;
        }
    }
}

