package com.sparta.newspeed.domain.user.service;

import com.sparta.newspeed.common.aws3.S3Service;
import com.sparta.newspeed.domain.auth.service.AuthService;
import com.sparta.newspeed.common.exception.CustomException;
import com.sparta.newspeed.common.exception.ErrorCode;
import com.sparta.newspeed.common.util.RedisUtil;
import com.sparta.newspeed.domain.user.dto.UserInfoUpdateDto;
import com.sparta.newspeed.domain.user.dto.UserPwRequestDto;
import com.sparta.newspeed.domain.user.dto.UserResponseDto;
import com.sparta.newspeed.domain.user.dto.UserStatusDto;
import com.sparta.newspeed.domain.user.entity.User;
import com.sparta.newspeed.domain.user.repository.UserRepository;
import com.sparta.newspeed.domain.user.entity.UserRoleEnum;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

@Service
@RequiredArgsConstructor
public class UserService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuthService authService;
    private final RedisUtil redisUtil;
    private final S3Service s3Service;

    public UserResponseDto getUser(Long userSeq) {
        User user = findById(userSeq);
        UserResponseDto responseDto = new UserResponseDto(user);
        responseDto.setPhotoUrl(s3Service.readFile(user.getPhotoName()));
        return responseDto;
    }

    public User findById(Long userSeq) {
        return userRepository.findById(userSeq).orElseThrow(
                () -> new CustomException(ErrorCode.USER_NOT_FOUND)
        );
    }

    @Transactional
    public UserInfoUpdateDto updateUser(Long userSeq, UserInfoUpdateDto requestDto, MultipartFile file) {
        User user = findById(userSeq);
        if (file != null) {
            s3Service.deleteFile(user.getPhotoName());
            user.setPhotoName(s3Service.uploadFile(file));
        }
        user.updateUserInfo(requestDto);
        return new UserInfoUpdateDto(user);
    }

    @Transactional
    public void updateUserPassword(Long userSeq, UserPwRequestDto requestDto) {
        User user = findById(userSeq);
        if (!passwordEncoder.matches(requestDto.getOldPassword(), user.getUserPassword())) {
            throw new CustomException(ErrorCode.INCORRECT_PASSWORD);
        }

        if (passwordEncoder.matches(requestDto.getNewPassword(), user.getUserPassword())) {
            throw new CustomException(ErrorCode.DUPLICATE_PASSWORD);
        }

        user.updatePassword(passwordEncoder.encode(requestDto.getNewPassword()));
    }

    @Transactional
    public void updateWithdraw(Long userSeq, UserStatusDto requestDto) {
        User user = findById(userSeq);

        // ID가 일치하지 않을 경우
        if(!requestDto.getUserId().equals(user.getUserId())){
            throw new CustomException(ErrorCode.USER_NOT_FOUND);
        }
        // PW가 일치하지 않을 경우
        if (!passwordEncoder.matches(requestDto.getPassword(), user.getUserPassword())) {
            throw new CustomException(ErrorCode.INCORRECT_PASSWORD);
        }
        // 회원이 이미 탈퇴 상태일 경우
        if (user.getRole().equals(UserRoleEnum.WITHDRAW)){
            throw new CustomException(ErrorCode.USER_NOT_VALID);
        }

        // 회원 상태를 탈퇴로 변경
        user.updateRole(UserRoleEnum.WITHDRAW);
    }

    public void mailSend(String userEmail) {
        authService.joinEmail(userEmail);
    }

    @Transactional
    public Boolean CheckAuthNum(Long userSeq, String email, String authNum) {
        User user = findById(userSeq);
            if (redisUtil.getData(authNum) == null) {
                return false;
            } else if (redisUtil.getData(authNum).equals(email)) {
                user.updateRole(UserRoleEnum.USER);
                return true;
            } else {
                return false;
            }
        }

}
