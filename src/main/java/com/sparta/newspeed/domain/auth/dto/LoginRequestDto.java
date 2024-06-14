package com.sparta.newspeed.domain.auth.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class LoginRequestDto {
    @NotBlank
    private String userId;
    @NotBlank
    private String password;
}