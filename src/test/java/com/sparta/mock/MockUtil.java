package com.sparta.mock;

import com.sparta.newspeed.domain.user.entity.User;
import com.sparta.newspeed.domain.user.entity.UserRoleEnum;
import com.sparta.newspeed.security.service.UserDetailsImpl;
import net.jqwik.api.Arbitraries;
import net.jqwik.api.arbitraries.StringArbitrary;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;

import java.security.Principal;

public class MockUtil {

    public static Principal makePrincipal() {
        // Mock 테스트 유져 생성
        Long userSeq = 1L;
        String username = "test1";
        String password = "1234";
        String email = "test1@test.com";
        UserRoleEnum role = UserRoleEnum.USER;
        // 가짜 유저 객체
        User testUser = User.builder()
                .userSeq(userSeq)
                .userId(username)
                .userPassword(password)
                .userEmail(email)
                .role(role)
                .build();

        return makePrincipal(testUser);
    }

    public static Principal makePrincipal(User testUser) {
        // UserDetails 직접 만들어줌
        UserDetailsImpl testUserDetails = new UserDetailsImpl(testUser);
        // 가짜 principal 객체 생성
        return new UsernamePasswordAuthenticationToken(testUserDetails, "", testUserDetails.getAuthorities());
    }

    public static MockMultipartFile getMockMultipartFileImage() {
        return getMockMultipartFile(MediaType.IMAGE_PNG_VALUE);
    }

    public static MockMultipartFile getMockMultipartFile(String mediaType) {
        return new MockMultipartFile(
                "file",
                "file.png",
                mediaType,
                "file".getBytes()
        );
    }
}
