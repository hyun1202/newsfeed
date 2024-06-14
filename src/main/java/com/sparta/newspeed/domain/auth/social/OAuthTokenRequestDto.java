package com.sparta.newspeed.domain.auth.social;

import lombok.Builder;
import lombok.Getter;

@Builder
@Getter
public class OAuthTokenRequestDto {
    private String grant_type;
    private String client_id;
    private String client_secret;
    private String code;
    private String state;
    private String refresh_token;
}
