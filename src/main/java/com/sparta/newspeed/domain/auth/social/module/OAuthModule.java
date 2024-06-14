package com.sparta.newspeed.domain.auth.social.module;

import java.net.URI;

public interface OAuthModule {
    public String getClientId();
    public String getClientSecret();
    public String getSocialLoginUri();
    public URI getAccessTokenUri(String code, String state);
    public URI getRefreshTokenUri(String refreshToken);
    public URI getSocialUserUri(String accessToken);
}
