package com.sparta.newspeed.domain.user.controller;

import com.sparta.newspeed.domain.user.dto.UserInfoUpdateDto;
import com.sparta.newspeed.domain.user.dto.UserPwRequestDto;
import com.sparta.newspeed.domain.user.dto.UserResponseDto;
import com.sparta.newspeed.domain.user.dto.UserStatusDto;
import com.sparta.newspeed.common.mail.dto.EmailCheckDto;
import com.sparta.newspeed.security.service.UserDetailsImpl;
import com.sparta.newspeed.domain.user.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@Tag(name = "User API", description = "User API 입니다")
@RestController
@RequestMapping("/api/users")
public class UserController {
    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping
    public UserResponseDto getUser(@AuthenticationPrincipal UserDetailsImpl userDetails) {
        return userService.getUser(userDetails.getUser().getUserSeq());
    }

    @PutMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public UserInfoUpdateDto updateUser(@AuthenticationPrincipal UserDetailsImpl userDetails,
                                        @RequestPart UserInfoUpdateDto requestDto,
                                        @RequestPart(required = false) MultipartFile file) {
        return userService.updateUser(userDetails.getUser().getUserSeq(), requestDto, file);
    }

    @PutMapping("/password")
    public ResponseEntity<String> updateUserPassword(@AuthenticationPrincipal UserDetailsImpl userDetails,
                                                     @RequestBody @Valid UserPwRequestDto requestDto) {
        userService.updateUserPassword(userDetails.getUser().getUserSeq(), requestDto);
        return ResponseEntity.ok("Password updated");
    }

    @Operation(summary = "회원탈퇴", description = "회원의 상태를 변경")
    @PostMapping("/withdraw")
    public ResponseEntity<String> updateWithdraw(@AuthenticationPrincipal UserDetailsImpl userDetails,
                                           @RequestBody @Valid UserStatusDto requestDto){
        userService.updateWithdraw(userDetails.getUser().getUserSeq(), requestDto);
        return ResponseEntity.ok("Update user withdraw");
    }
    @Operation(summary = "reMailSend",description = "인증되지않은 이메일 인증 메일을 보내는 api 입니다.")
    @PostMapping("/reMailSend")
    public ResponseEntity<String> reMailSend(@AuthenticationPrincipal UserDetailsImpl userDetails){
        userService.mailSend(userDetails.getUser().getUserEmail());
        return ResponseEntity.ok().body("인증 메일을 전송하였습니다.");
    }

    @Operation(summary = "reMailCheck",description = "인증되지않은 이메일 인증 api 입니다.")
    @PostMapping("/reMailCheck")
    public ResponseEntity<String> reMailCheck(@AuthenticationPrincipal UserDetailsImpl userDetails, @RequestBody @Valid EmailCheckDto emailCheckDto){
        Boolean Checked = userService.CheckAuthNum(userDetails.getUser().getUserSeq(), emailCheckDto.getEmail(),emailCheckDto.getAuthNum());
        if(Checked){
            return ResponseEntity.ok().body("이메일 인증 성공");
        }else{
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("이메일 인증 실패");
        }
    }
}
