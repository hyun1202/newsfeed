package com.sparta.newspeed.auth.controller;

import com.sparta.newspeed.auth.dto.*;
import com.sparta.newspeed.auth.service.AuthService;
import com.sparta.newspeed.mail.dto.EmailCheckDto;
import com.sparta.newspeed.security.service.UserDetailsImpl;
import com.sparta.newspeed.security.util.JwtUtil;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;


@Tag(name = "인증 API",description = "인증 API")
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/auth")
public class AuthController {

    private final AuthService authService;

    @PostMapping("/login")
    public ResponseEntity<TokenResponseDto> login(@RequestBody LoginRequestDto requestDto, HttpServletResponse response) {
        TokenResponseDto token = authService.login(requestDto);
        response.addHeader(JwtUtil.AUTHORIZATION_HEADER, token.getAccessToken());
//        response.addHeader(JwtUtil.AUTHORIZATION_HEADER + "refresh", token.getRefreshToken());
//        return "로그인 성공" + response.getStatus();
        return ResponseEntity.ok().body(token);
    }

    @Operation(summary = "회원가입",description = "회원가입")
    @PostMapping("/signup")
    public SignupResponseDto Signup(@RequestBody @Valid SignUpRequestDto requestDto){
        return authService.signup(requestDto);
    }

    @PostMapping("/signup/mailCheck")
    public ResponseEntity<String> AuthCheck(@RequestBody @Valid EmailCheckDto emailCheckDto){
        Boolean Checked=authService.CheckAuthNum(emailCheckDto.getEmail(),emailCheckDto.getAuthNum());
        if(Checked){
            return ResponseEntity.ok().body("이메일 인증 성공");
        }else{
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("이메일 인증 실패");
        }
    }

    @PostMapping("/reauth")
    public ResponseEntity<TokenResponseDto> reAuth(@RequestBody TokenRequestDto requestDto, HttpServletResponse response) {
        String refreshtoken = requestDto.getRefreshToken();
        TokenResponseDto newToken = authService.reAuth(refreshtoken);
        response.addHeader(JwtUtil.AUTHORIZATION_HEADER, newToken.getAccessToken());

        return ResponseEntity.ok().body(newToken);
    }
    @PostMapping("/logout")
    public String logout(@AuthenticationPrincipal UserDetailsImpl userDetails, HttpServletRequest request, HttpServletResponse response) {
        request.removeAttribute(JwtUtil.AUTHORIZATION_HEADER);
        authService.logout(userDetails.getUser());
        return "로그아웃 성공" + response.getStatus();
    }
}
