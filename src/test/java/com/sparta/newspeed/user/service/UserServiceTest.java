package com.sparta.newspeed.user.service;

import com.sparta.newspeed.domain.user.dto.UserPwRequestDto;
import com.sparta.newspeed.domain.user.dto.UserResponseDto;
import com.sparta.newspeed.domain.user.entity.User;
import com.sparta.newspeed.domain.user.entity.UserRoleEnum;
import com.sparta.newspeed.domain.user.repository.UserRepository;
import com.sparta.newspeed.domain.user.service.UserService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.transaction.annotation.Transactional;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest(properties = "spring.profiles.active:test")
class UserServiceTest {

    @Autowired
    private UserService userService;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

//    @BeforeEach
//    void init() {
//        User user = User.builder()
//                .userId("test1Id")
//                .userPassword("password")
//                .userEmail("test1@test.com")
//                .userIntro("test1입니다.")
//                .userName("test1")
//                .role(UserRoleEnum.USER)
//                .build();
//        userRepository.save(user);
//    }

    @DisplayName("유저를 가져온다")
    @Transactional(readOnly = true)
    @Test
    void getUser() {
        UserResponseDto getUser = userService.getUser(1L);

        assertEquals(getUser.getId(), "test1Id");
        assertEquals(getUser.getEmail(), "test1@test.com");
    }

    @DisplayName("유저를 가져온다 (findById)")
    @Test
    @Transactional(readOnly = true)
    void findById() {
        User user = userService.findById(1L);

        assertEquals(user.getUserId(), "test1Id");
    }

    @DisplayName("유저 정보를 변경한다.")
    @Test
    @Transactional
    void updateUser() {
//        UserInfoUpdateDto updateUser = new UserInfoUpdateDto(
//                "updateName",
////                "test1@test1.com",
//                "update_intro"
//        );
//        //userService.updateUser(1L, updateUser);
//
//        User user = userService.findById(1L);
//
//        assertEquals(updateUser.getName(), user.getUserName());
    }

    @DisplayName("유저 비밀번호를 변경한다.")
    @Test
    void updateUserPassword() {
        User createUser = User.builder()
                .userId("test1Id")
                .userPassword("$2a$10$kNW.vlg0oQtNIiqU4LUR8eL3XkvdDc3WqB2kshkHZrjqhM/nj9UMS")
                .userEmail("test1@test.com")
                .userIntro("test1입니다.")
                .userName("test1")
                .role(UserRoleEnum.USER)
                .build();
        userRepository.save(createUser);

        UserPwRequestDto requestDto = new UserPwRequestDto(
                "password",
                "newPassword"
        );

        userService.updateUserPassword(1L, requestDto);

        User user = userService.findById(1L);

        assertEquals(passwordEncoder.matches("newPassword", user.getUserPassword()), true);
    }
}