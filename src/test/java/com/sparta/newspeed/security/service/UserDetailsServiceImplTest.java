package com.sparta.newspeed.security.service;

import com.sparta.newspeed.NewsfeedApplicationTests;
import com.sparta.newspeed.domain.user.entity.User;
import com.sparta.newspeed.domain.user.entity.UserRoleEnum;
import com.sparta.newspeed.domain.user.repository.UserRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.test.mock.mockito.SpyBean;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.BDDMockito.given;


class UserDetailsServiceImplTest extends NewsfeedApplicationTests {

    @Autowired
    UserDetailsServiceImpl userDetailsService;

    @SpyBean
    UserRepository userRepository;

    @DisplayName("UserDetailsService 통합 테스트")
    @Test
    void test1() {
        userDataInit();
        List<User> findUser = userRepository.findAll();

        // given
        String userId = findUser.get(0).getUserId();

        // when
        UserDetails userDetails = userDetailsService.loadUserByUsername(userId);

        // then
        assertNotNull(userDetails);
        assertEquals(userId, userDetails.getUsername());
    }

    @DisplayName("UserDetailsService 정상 단위 테스트")
    @Test
    void test2() {
        // given
        String userId = "test1";

        User user = User.builder()
                .userId(userId)
                .role(UserRoleEnum.USER)
                .build();

        // return 값 지정
        given(userRepository.findByUserId(userId)).willReturn(Optional.of(user));

        // when
        UserDetails userDetails = userDetailsService.loadUserByUsername(userId);

        // then
        assertNotNull(userDetails);
        assertEquals(userId, userDetails.getUsername());
    }

    @DisplayName("UserDetailsService 탈퇴 회원 단위 테스트")
    @Test
    void test3() {
        // given
        String userId = "test1";

        User user = User.builder()
                .userId(userId)
                .role(UserRoleEnum.WITHDRAW)
                .build();

        // return 값 지정
        given(userRepository.findByUserId(userId)).willReturn(Optional.of(user));

        // when - then
        assertThrows(UsernameNotFoundException.class,
                () -> userDetailsService.loadUserByUsername(userId));
    }

    @DisplayName("UserDetailsService 존재하지 않는 회원 테스트")
    @Test
    void test4() {
        userDataInit();
        // given
        String userId = "test2";

        // when - then
        assertThrows(UsernameNotFoundException.class,
                () -> userDetailsService.loadUserByUsername(userId));
    }
}