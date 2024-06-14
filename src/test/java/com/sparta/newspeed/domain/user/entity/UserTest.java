package com.sparta.newspeed.domain.user.entity;

import com.sparta.fixturemonkey.FixtureMonkeyUtil;
import com.sparta.newspeed.domain.user.dto.UserInfoUpdateDto;
import net.jqwik.api.Arbitraries;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class UserTest {

    User user;

    @BeforeEach
    void setUp() {
        user = FixtureMonkeyUtil.Entity.toUser();
    }

    @DisplayName("유저 정보 업데이트 확인")
    @Test
    void test1() {
        // given
        String userName = "updateUserName";
        String userIntro = "updateUserIntro";

        // when
        user.updateUserInfo(new UserInfoUpdateDto(userName, userIntro));

        // then
        assertEquals(userName, user.getUserName());
        assertEquals(userIntro, user.getUserIntro());
    }

    @DisplayName("유저 소셜로그인 정보 업데이트 확인")
    @Test
    void test2() {
        // given
        String userName = "updateUserName";
        String profileImageUrl = "updateUserProfileImageUrl";

        // when
        User updateUser = user.updateOAuth2Info(userName, profileImageUrl);

        // then
        assertEquals(userName, updateUser.getUserName());
        assertEquals(profileImageUrl, updateUser.getProfileImageUrl());
    }

    @DisplayName("패스워드 업데이트 확인")
    @Test
    void test3() {
        // given
        String password = "password";

        // when
        user.updatePassword(password);

        // then
        assertEquals(password, user.getUserPassword());
    }

    @DisplayName("권한 업데이트 확인")
    @Test
    void test4() {
        // given
        UserRoleEnum updateRole = UserRoleEnum.ADMIN;

        // when
        user.updateRole(updateRole);

        // then
        assertEquals(updateRole, user.getRole());
    }

    @DisplayName("리프레시 토큰 업데이트 확인")
    @Test
    void test5() {
        // given
        String refreshToken = "refreshToken";

        // when
        user.setRefreshToken(refreshToken);

        // then
        assertEquals(refreshToken, user.getRefreshToken());
    }

    @DisplayName("사진명 변경 업데이트 확인")
    @Test
    void test6() {
        // given
        String photoName = "updatePhotoName";

        // when
        user.setPhotoName(photoName);

        // then
        assertEquals(photoName, user.getPhotoName());
    }
}