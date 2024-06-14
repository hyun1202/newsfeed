package com.sparta.newspeed.domain.auth.controller;

import com.sparta.newspeed.domain.auth.dto.TokenResponseDto;
import com.sparta.newspeed.domain.auth.service.SocialService;
import com.sparta.newspeed.domain.auth.social.SocialEnum;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.view.RedirectView;

@RequiredArgsConstructor
@Controller
@RequestMapping("/api/social")
public class SocialController {
    private final SocialService socialService;

    /**
     * 소셜 로그인 페이지 redirect
     * 각 소셜 서비스의 인가코드 발급 이후 자동으로 callback url으로 redirect된다.
     *
     * @param socialEnum 소셜명
     * @return 소셜 로그인 페이지
     */
    @GetMapping("login/{socialName}")
    public RedirectView redirectLogin(@PathVariable("socialName") SocialEnum socialEnum) {
        return new RedirectView(socialService.getSocialLoginUrl(socialEnum));
    }


    /**
     * 소셜로그인 이후 callback을 받는다.
     * 인가코드와 state를 받은 후에 해당 API를 호출하여 사용
     *
     * @param code 인가코드
     * @param state 필수X
     * @return 서비스 토큰값
     */
    @GetMapping("callback/{socialName}")
    @ResponseBody
    public TokenResponseDto socialLogin(@PathVariable("socialName") SocialEnum socialEnum,
                                     @RequestParam("code") String code,
                                     @RequestParam(value = "state", required = false) String state) {
        return socialService.socialLogin(socialEnum, code, state);
    }
}
