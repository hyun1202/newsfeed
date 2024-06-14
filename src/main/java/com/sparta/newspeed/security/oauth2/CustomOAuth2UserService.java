package com.sparta.newspeed.security.oauth2;

import com.sparta.newspeed.domain.auth.service.AuthService;
import com.sparta.newspeed.domain.auth.social.OAuthAttributes;
import com.sparta.newspeed.domain.user.entity.User;
import lombok.RequiredArgsConstructor;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserService;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service
@RequiredArgsConstructor
public class CustomOAuth2UserService implements OAuth2UserService<OAuth2UserRequest, OAuth2User> {
    private final AuthService authService;

    @Override
    public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {
        OAuth2UserService<OAuth2UserRequest, OAuth2User> service = new DefaultOAuth2UserService();
        // OAuth2 정보를 가져온다.
        OAuth2User oAuth2User = service.loadUser(userRequest);

        // OAuth2User의 attribute
        Map<String, Object> originAttributes = oAuth2User.getAttributes();

        // 소셜 정보
        String registrationId = userRequest.getClientRegistration().getRegistrationId();
        OAuthAttributes attributes = OAuthAttributes.of(registrationId, originAttributes);
        User user = saveOrUpdate(attributes);

        return OAuth2CustomUser.builder()
                .registrationId(registrationId)
                .attributes(originAttributes)
                .user(user)
                .build();
    }

    /**
     * 존재하는 회원이라면 이름과 프로필 이미지 업데이트
     * 처음 가입하는 회원이라면 User 데이터 생성
     *
     * @param attributes 소셜 정보
     * @return 업데이트 & 생성된 User 데이터
     */
    private User saveOrUpdate(OAuthAttributes attributes) {
        return authService.saveOrUpdateOAuth2Info(attributes);
    }
}
