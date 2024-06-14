package com.sparta.newspeed.domain.user.dto;

import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Getter
public class UserPwRequestDto {
    @Pattern(regexp = "^(?=.*[a-zA-Z])(?=.*[0-9])(?=.*[`~!@#$%^])[a-zA-z\\d`~!@#$%^]{10,}$", message = "oldPassword의 비밀번호 형식이 올바르지 않습니다.")
    private String oldPassword;
    @Pattern(regexp = "^(?=.*[a-zA-Z])(?=.*[0-9])(?=.*[`~!@#$%^])[a-zA-z\\d`~!@#$%^]{10,}$", message = "newPassword의 비밀번호 형식이 올바르지 않습니다.")
    private String newPassword;
}
