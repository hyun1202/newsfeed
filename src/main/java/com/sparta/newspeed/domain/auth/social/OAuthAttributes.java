package com.sparta.newspeed.domain.auth.social;

import com.sparta.newspeed.domain.user.entity.User;
import com.sparta.newspeed.domain.user.entity.UserRoleEnum;
import lombok.Builder;
import lombok.Getter;
import lombok.ToString;

import java.util.Map;

@Getter
@ToString
public class OAuthAttributes {
    private Map<String, Object> attributes;     // OAuth2 반환하는 유저 정보
    private String nameAttributesKey;
    private String name;
    private String email;
    private String gender;
    private String ageRange;
    private String profileImageUrl;

    @Builder
    public OAuthAttributes(Map<String, Object> attributes, String nameAttributesKey,
                           String name, String email, String gender, String ageRange, String profileImageUrl) {
        this.attributes = attributes;
        this.nameAttributesKey = nameAttributesKey;
        this.name = name;
        this.email = email;
        this.gender = gender;
        this.ageRange = ageRange;
        this.profileImageUrl = profileImageUrl;
    }

    public static OAuthAttributes of(String socialName, Map<String, Object> attributes) {
        if ("kakao".equals(socialName)) {
            return ofKakao("id", attributes);
        } else if ("google".equals(socialName)) {
            return ofGoogle("sub", attributes);
        } else if ("naver".equals(socialName)) {
            return ofNaver("id", attributes);
        }

        return null;
    }

    private static OAuthAttributes ofGoogle(String userNameAttributeName, Map<String, Object> attributes) {
        return OAuthAttributes.builder()
                .name(String.valueOf(attributes.get("name")))
                .email(String.valueOf(attributes.get("email")))
                .profileImageUrl(String.valueOf(attributes.get("picture")))
                .attributes(attributes)
                .nameAttributesKey(userNameAttributeName)
                .build();
    }

    private static OAuthAttributes ofKakao(String userNameAttributeName, Map<String, Object> attributes) {
        Map<String, Object> kakaoAccount = (Map<String, Object>) attributes.get("kakao_account");
        Map<String, Object> kakaoProfile = (Map<String, Object>) kakaoAccount.get("profile");

        return OAuthAttributes.builder()
                .name(String.valueOf(kakaoProfile.get("nickname")))
                .email(String.valueOf(kakaoAccount.get("email")))
                .gender(String.valueOf(kakaoAccount.get("gender")))
                .ageRange(String.valueOf(kakaoAccount.get("age_range")))
                .profileImageUrl(String.valueOf(kakaoProfile.get("profile_image_url")))
                .nameAttributesKey(userNameAttributeName)
                .attributes(attributes)
                .build();
    }

    public static OAuthAttributes ofNaver(String userNameAttributeName, Map<String, Object> attributes) {
        Map<String, Object> response = (Map<String, Object>) attributes.get("response");

        return OAuthAttributes.builder()
                .name(String.valueOf(response.get("nickname")))
                .email(String.valueOf(response.get("email")))
                .profileImageUrl(String.valueOf(response.get("profile_image")))
                .ageRange((String) response.get("age"))
                .gender((String) response.get("gender"))
                .attributes(response)
                .nameAttributesKey(userNameAttributeName)
                .build();
    }

    public User toEntity() {
        return User.builder()
                .userId(email)
                .userPassword("social")
                .userName(name)
                .userEmail(email)
                .role(UserRoleEnum.SOCIAL)
                .build();
    }
}