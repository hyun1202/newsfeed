package com.sparta.newspeed.domain.user.dto;

import com.sparta.newspeed.domain.user.entity.User;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Getter
public class UserInfoUpdateDto {
    @Schema(description = "유저명")
    private String name;
    @Schema(description = "유저 한줄 소개")
    private String intro;

    public UserInfoUpdateDto(User user) {
        this.name = user.getUserName();
        this.intro = user.getUserIntro();
    }
}
