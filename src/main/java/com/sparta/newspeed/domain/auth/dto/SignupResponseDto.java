package com.sparta.newspeed.domain.auth.dto;

import com.sparta.newspeed.domain.user.entity.User;
import com.sparta.newspeed.domain.user.entity.UserRoleEnum;
import lombok.Getter;

@Getter
public class SignupResponseDto {
    private Long userSeq;
    private String userName;
    private String userId;
    private UserRoleEnum role;

    public SignupResponseDto(User user){
        this.userSeq = user.getUserSeq();
        this.userName = user.getUserName();
        this.userId = user.getUserId();
        this.role = user.getRole();
    }
}
