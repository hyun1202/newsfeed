package com.sparta.newspeed.domain.auth.controller;

import com.sparta.mock.MockUtil;
import com.sparta.newspeed.common.aws3.S3Service;
import com.sparta.newspeed.common.util.JwtUtil;
import com.sparta.newspeed.common.util.RedisUtil;
import com.sparta.newspeed.domain.auth.dto.SignUpRequestDto;
import com.sparta.newspeed.domain.auth.service.AuthService;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.jpa.mapping.JpaMetamodelMappingContext;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import java.util.*;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@MockBean(JpaMetamodelMappingContext.class)
@WebMvcTest(controllers = {AuthController.class})
class AuthControllerTest {
    private MockMvc mockMvc;
    private ValidatorFactory factory;
    private Validator validator;

    @Autowired
    private WebApplicationContext webApplicationContext;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext).build();
        factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
    }

    @MockBean
    AuthService authService;

    @MockBean
    PasswordEncoder passwordEncoder;

    @MockBean
    JavaMailSender mailSender;

    @MockBean
    JwtUtil jwtUtil;

    @MockBean
    S3Service s3Service;

    @MockBean
    RedisUtil redisUtil;

    @DisplayName("회원가입")
    @Test
    void test1() throws Exception {
        // given
        MockMultipartFile file = MockUtil.getMockMultipartFileImage();
        String userId = "test1test1";
        String password = "qwertyuiop2!";
        String uesrName = "test1";
        String email = "test1@test.com";

        // when - then
        mockMvc.perform(MockMvcRequestBuilders
                        .multipart("/api/auth/signup") // URL 설정
                        .file(file) // 파일 전송
                        .param("userId", userId) // 폼 데이터
                        .param("password", password)
                        .param("userName", uesrName)
                        .param("email", email)
                )
                .andExpect(status().isOk());
    }

    @DisplayName("회원가입 validation 테스트")
    @Test
    void test2() {
        // given
        MockMultipartFile file = MockUtil.getMockMultipartFileImage();
        String userId = "test1";
        String password = "1234";
        String uesrName = "";
        String email = "notEmail";

        SignUpRequestDto requestDto = new SignUpRequestDto(
                userId,
                password,
                uesrName,
                email,
                file
        );

        // when
        Set<ConstraintViolation<SignUpRequestDto>> violations = validator.validate(requestDto);

        // then
        List<String> errorMessages = new ArrayList<>(
                Arrays.asList(
                        "이메일 형식이 아닙니다.",
                        "비밀번호는 대소문자 포함 영문 + 숫자 + 특수문자를 최소 1글자씩 포함해야 합니다.",
                        "닉네임은 null이 들어올 수 없습니다.",
                        "아이디는 대소문자 포함 영문 + 숫자만을 허용합니다.(10 ~ 20)"
                )
        );

        List<ConstraintViolation<SignUpRequestDto>> constraintViolations = violations.stream().toList();

        for (ConstraintViolation<SignUpRequestDto> constraintViolation : constraintViolations) {
            for (String errorMessage : errorMessages) {
                if (Objects.equals(constraintViolation.getMessage(), errorMessage)) {
                    assertEquals(constraintViolation.getMessage(), errorMessage);
                    errorMessages.remove(errorMessage);
                    break;
                }
            }
        }

        assertTrue(errorMessages.isEmpty());
    }
}