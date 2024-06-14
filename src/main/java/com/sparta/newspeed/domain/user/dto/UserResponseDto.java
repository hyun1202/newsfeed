package com.sparta.newspeed.domain.user.dto;

import com.sparta.newspeed.domain.user.entity.User;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@Getter
public class UserResponseDto {
    @Schema(description = "유저 아이디")
    private String id;
    @Schema(description = "유저명")
    private String name;
    @Schema(description = "한줄 소개")
    private String intro;
    @Schema(description = "이메일")
    private String email;
    @Schema(description = "프로필 사진 URL")
    private String photoUrl;

    public UserResponseDto(User user) {
        this.id = user.getUserId();
        this.name = user.getUserName();
        this.intro = user.getUserIntro();
        this.email = user.getUserEmail();
    }
    public void setPhotoUrl(String photoUrl) {
        this.photoUrl = photoUrl;
    }
}
