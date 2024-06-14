package com.sparta.newspeed.domain.auth.dto;

import com.sparta.newspeed.domain.user.entity.UserRoleEnum;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TokenResponseDto {
    private UserRoleEnum grantType;
    private String accessToken;
    private String refreshToken;
    private String key;
}
