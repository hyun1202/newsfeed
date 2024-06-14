package com.sparta.newspeed.domain.user.entity;

import com.sparta.newspeed.common.Timestamped;
import com.sparta.newspeed.domain.user.dto.UserInfoUpdateDto;
import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.*;

import java.time.LocalDateTime;

@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
@Entity
@Getter
@Table(name = "users")
public class User extends Timestamped {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "user_seq")
    private Long userSeq;

    @NotBlank
    @Column(name = "user_id")
    private String userId;

    @NotBlank
    @Column(name = "user_password")
    private String userPassword;

    @Column(name = "user_name")
    private String userName;

    @NotBlank
    @Email
    @Column(name = "user_email")
    private String userEmail;

    @Column(name = "user_intro")
    private String userIntro;

    @Column(name = "user_status")
    private String userStatus;

    @Column(name = "profile_image_url")
    private String profileImageUrl;

    @Column(nullable = false)
    @Enumerated(value = EnumType.STRING)
    private UserRoleEnum role;

    @Column(name = "refresh_token")
    private String refreshToken;

    @Column(name = "status_modified")
    private LocalDateTime statusModified;

    @Column(name = "photo_name")
    private String photoName;

    public User(String userId, String userPassword, UserRoleEnum role) {
        this.userId = userId;
        this.userPassword = userPassword;
        this.role = role;
    }

    public void updateUserInfo(UserInfoUpdateDto requestDto) {
        this.userName = requestDto.getName();
        this.userIntro = requestDto.getIntro();
    }

    public User updateOAuth2Info(String userName, String profileImageUrl) {
        this.userName = userName;
        this.profileImageUrl = profileImageUrl;
        return this;
    }

    public void updatePassword(String encNewPassword) {
        this.userPassword = encNewPassword;
    }

    public void updateRole(UserRoleEnum role){
        this.role = role;
    }

    public void setRefreshToken(String token) {
        this.refreshToken = token;
    }

    public void setPhotoName(String photoName) {
        this.photoName = photoName;
    }
}
