package com.sparta.newspeed.domain.user.dto;

import jakarta.validation.constraints.Pattern;
import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@Getter
public class UserStatusDto {
    private String userId;
    @Pattern(regexp = "^(?=.*[a-zA-Z])(?=.*[0-9])(?=.*[`~!@#$%^])[a-zA-z\\d`~!@#$%^]{10,}$", message = "newPassword의 비밀번호 형식이 올바르지 않습니다.")
    private String password;
}
