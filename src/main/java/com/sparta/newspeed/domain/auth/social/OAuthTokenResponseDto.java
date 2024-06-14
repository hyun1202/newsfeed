package com.sparta.newspeed.domain.auth.social;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonRootName;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@JsonInclude(JsonInclude.Include.NON_NULL) // null 값은 무시
@JsonRootName("") // 루트 이름을 없앰
@AllArgsConstructor
@NoArgsConstructor
@Getter
public class OAuthTokenResponseDto {
    private String access_token;
    private String refresh_token;
    private String token_type;
    private String expires_in;
}

