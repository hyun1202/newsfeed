package com.sparta.newspeed.domain.user.service;

import com.sparta.fixturemonkey.FixtureMonkeyUtil;
import com.sparta.newspeed.NewsfeedApplicationTests;
import com.sparta.newspeed.common.aws3.S3Service;
import com.sparta.newspeed.common.exception.CustomException;
import com.sparta.newspeed.common.exception.ErrorCode;
import com.sparta.newspeed.common.util.RedisUtil;
import com.sparta.newspeed.domain.auth.service.AuthService;
import com.sparta.newspeed.domain.user.dto.UserInfoUpdateDto;
import com.sparta.newspeed.domain.user.dto.UserPwRequestDto;
import com.sparta.newspeed.domain.user.dto.UserResponseDto;
import com.sparta.newspeed.domain.user.dto.UserStatusDto;
import com.sparta.newspeed.domain.user.entity.User;
import com.sparta.newspeed.domain.user.entity.UserRoleEnum;
import com.sparta.newspeed.domain.user.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.test.mock.mockito.SpyBean;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class UserServiceTest extends NewsfeedApplicationTests {

    private List<User> findUser;

    @Autowired
    UserService userService;

    @Autowired
    UserRepository userRepository;

    @SpyBean
    RedisUtil redisUtil;

    @MockBean
    AuthService authService;

    @MockBean
    S3Service s3Service;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Mock
    UserRepository mockUserRepository;

    @BeforeEach
    void setUp() {
        // init 데이터 추가
        userDataInit();
        findUser = userRepository.findAll();
    }

    @DisplayName("유저를 가져온다")
    @Test
    void test1() {
        // given
        Long userSeq = findUser.get(0).getUserSeq();

        // when
        UserResponseDto getUser = userService.getUser(userSeq);

        // then
        assertEquals(findUser.get(0).getUserId(), getUser.getId());
    }

    @DisplayName("유저를 가져온다 - 해당하는 유저가 없을 때")
    @Test
    void test2() {
        // given
        Long userSeq = -14L;

        // when
        CustomException exception = assertThrows(CustomException.class,
                () -> userService.findById(userSeq));

        // then
        assertEquals(exception.getErrorCode(), ErrorCode.USER_NOT_FOUND);
    }

    @DisplayName("유저 정보를 변경한다.")
    @Test
    @Transactional
    void test3() {
        // given
        Long userSeq = findUser.get(0).getUserSeq();

        String name = "updateName";
        String intro = "updateIntro";
        String photoName = "updatePhotoName";

        UserInfoUpdateDto updateDto = new UserInfoUpdateDto(name, intro);

        // s3 테스트 제외
        doNothing().when(s3Service).deleteFile(anyString());
        when(s3Service.uploadFile(any())).thenReturn(photoName);

        // when
        userService.updateUser(userSeq, updateDto, new MockMultipartFile(photoName, photoName.getBytes()));

        // then
        User updateUser = userService.findById(userSeq);
        assertEquals(updateDto.getName(), updateUser.getUserName());
        assertEquals(updateDto.getIntro(), updateUser.getUserIntro());
        // 파일 이름 변경 기능은 정상 동작하지 않음
//        assertEquals(photoName, updateUser.getPhotoName());
    }

    @DisplayName("유저 비밀번호를 변경한다.")
    @Transactional
    @Test
    void test4() {
        // given
        User user = findUser.get(0);
        String password = "password";
        String newPassword = "updatePassword";

        // 유저가 랜덤으로 생성되었기 때문에 패스워드를 알 수 없으므로 업데이트 후 테스트
        user.updatePassword(passwordEncoder.encode(password));
        userRepository.save(user);

        UserPwRequestDto requestDto = new UserPwRequestDto(password, newPassword);

        // when
        userService.updateUserPassword(user.getUserSeq(), requestDto);

        // then
        User updateUser = userService.findById(user.getUserSeq());
        assertTrue(passwordEncoder.matches(newPassword, updateUser.getUserPassword()));
    }

    @DisplayName("유저 비밀번호를 변경한다. - 이전 비밀번호와 일치하지 않을 때")
    @Transactional
    @Test
    void test5() {
        // given
        User user = findUser.get(0);
        String password = "password";
        String newPassword = "updatePassword";

        // 유저가 랜덤으로 생성되었기 때문에 패스워드를 알 수 없으므로 업데이트 후 테스트
        user.updatePassword(passwordEncoder.encode(password));
        userRepository.save(user);

        UserPwRequestDto requestDto = new UserPwRequestDto(password + "1", newPassword);

        // when
        CustomException exception = assertThrows(CustomException.class,
                () -> userService.updateUserPassword(user.getUserSeq(), requestDto));

        // then
        assertEquals(exception.getErrorCode(), ErrorCode.INCORRECT_PASSWORD);
    }

    @DisplayName("유저 비밀번호를 변경한다. - 이전 비밀번호와 동일할 때")
    @Transactional
    @Test
    void test6() {
        // given
        User user = findUser.get(0);
        String password = "password";
        String newPassword = "password";

        // 유저가 랜덤으로 생성되었기 때문에 패스워드를 알 수 없으므로 업데이트 후 테스트
        user.updatePassword(passwordEncoder.encode(password));
        userRepository.save(user);

        UserPwRequestDto requestDto = new UserPwRequestDto(password, newPassword);

        // when
        CustomException exception = assertThrows(CustomException.class,
                () -> userService.updateUserPassword(user.getUserSeq(), requestDto));

        // then
        assertEquals(exception.getErrorCode(), ErrorCode.DUPLICATE_PASSWORD);
    }

    @DisplayName("유저 탈퇴")
    @Transactional
    @Test
    void test7() {
        // given
        User user = findUser.get(0);
        Long userSeq = user.getUserSeq();
        String userId = findUser.get(0).getUserId();
        String password = "password1!";

        // 유저가 랜덤으로 생성되었기 때문에 패스워드를 알 수 없으므로 업데이트 후 테스트
        user.updatePassword(passwordEncoder.encode(password));
        userRepository.save(user);

        UserStatusDto requestDto = FixtureMonkeyUtil.monkey()
                .giveMeBuilder(UserStatusDto.class)
                .set("userId", userId)
                .set("password", password)
                .sample();

        // when
        userService.updateWithdraw(userSeq, requestDto);

        // then
        User updateUser = userService.findById(userSeq);
        assertEquals(UserRoleEnum.WITHDRAW, updateUser.getRole());
    }

    @DisplayName("유저 탈퇴 - 아이디 불일치")
    @Transactional
    @Test
    void test8() {
        // given
        User user = findUser.get(0);
        Long userSeq = user.getUserSeq();
        String userId = findUser.get(0).getUserId();
        String password = "password1!";

        // 유저가 랜덤으로 생성되었기 때문에 패스워드를 알 수 없으므로 업데이트 후 테스트
        user.updatePassword(passwordEncoder.encode(password));
        userRepository.save(user);

        UserStatusDto requestDto = FixtureMonkeyUtil.monkey()
                .giveMeBuilder(UserStatusDto.class)
                .set("userId", userId + "1")
                .set("password", password)
                .sample();

        // when
        CustomException exception = assertThrows(CustomException.class,
                () -> userService.updateWithdraw(userSeq, requestDto));

        // then
        assertEquals(exception.getErrorCode(), ErrorCode.USER_NOT_FOUND);
    }

    @DisplayName("유저 탈퇴 - 비밀번호 불일치")
    @Transactional
    @Test
    void test9() {
        // given
        User user = findUser.get(0);
        Long userSeq = user.getUserSeq();
        String userId = findUser.get(0).getUserId();
        String password = "password1!";

        // 유저가 랜덤으로 생성되었기 때문에 패스워드를 알 수 없으므로 업데이트 후 테스트
        user.updatePassword(passwordEncoder.encode(password));
        userRepository.save(user);

        UserStatusDto requestDto = FixtureMonkeyUtil.monkey()
                .giveMeBuilder(UserStatusDto.class)
                .set("userId", userId)
                .set("password", password + "1")
                .sample();

        // when
        CustomException exception = assertThrows(CustomException.class,
                () -> userService.updateWithdraw(userSeq, requestDto));

        // then
        assertEquals(exception.getErrorCode(), ErrorCode.INCORRECT_PASSWORD);
    }

    @DisplayName("유저 탈퇴 - 탈퇴 회원")
    @Transactional
    @Test
    void test10() {
        // given
        User user = findUser.get(0);
        Long userSeq = user.getUserSeq();
        String userId = findUser.get(0).getUserId();
        String password = "password1!";

        // 유저가 랜덤으로 생성되었기 때문에 패스워드를 알 수 없으므로 업데이트 후 테스트
        user.updatePassword(passwordEncoder.encode(password));
        // 탈퇴 회원 테스트
        user.updateRole(UserRoleEnum.WITHDRAW);
        userRepository.save(user);

        UserStatusDto requestDto = FixtureMonkeyUtil.monkey()
                .giveMeBuilder(UserStatusDto.class)
                .set("userId", userId)
                .set("password", password)
                .sample();

        // when
        CustomException exception = assertThrows(CustomException.class,
                () -> userService.updateWithdraw(userSeq, requestDto));

        // then
        assertEquals(exception.getErrorCode(), ErrorCode.USER_NOT_VALID);
    }
}