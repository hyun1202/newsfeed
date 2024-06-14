package com.sparta.newspeed.domain.newsfeed.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;

@Getter
public class NewsfeedRequestDto {
    @Schema(description = "뉴스피드 제목")
    @NotBlank(message = "제목을 작성해주세요.")
    private String title;

    @Schema(description = "뉴스피드 내용")
    @NotBlank(message = "내용을 작성해주세요.")
    private String content;

    @Schema(description = "OTT 이름")
    @NotBlank(message = "OTT를 선택해주세요.")
    private String ottName;

    @Schema(description = "남은 인원")
    @Min(value = 1, message = "1명 이상으로 입력해주세요.")
    private int remainMember;
}
