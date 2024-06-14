package com.sparta.newspeed.domain.newsfeed.controller;

import com.sparta.newspeed.domain.newsfeed.dto.NewsfeedRequestDto;
import com.sparta.newspeed.domain.newsfeed.dto.NewsfeedResponseDto;
import com.sparta.newspeed.domain.newsfeed.service.NewsfeedService;
import com.sparta.newspeed.security.service.UserDetailsImpl;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@Tag(name = "Newsfeed API", description = "Newsfeed API 입니다")
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/newsfeeds")
public class NewsfeedController {

    private final NewsfeedService newsfeedService;

    @Operation(summary = "createNewsFeed", description = "뉴스피드 생성 기능입니다.")
    @PostMapping
    public ResponseEntity<NewsfeedResponseDto> createNewsFeed(@Valid @RequestBody NewsfeedRequestDto requestDto, @AuthenticationPrincipal UserDetailsImpl userDetails){
        return ResponseEntity.status(HttpStatus.CREATED).body(newsfeedService.createNewsFeed(requestDto,userDetails.getUser()));
    }

    @Operation(summary = "getNewsfeeds", description = "뉴스피드 전체조회 기능입니다. 페이지네이션, 정렬 및 기간별 검색을 지원합니다.")
    @GetMapping
    public ResponseEntity<List<NewsfeedResponseDto>> getNewsfeeds(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "createdAt") String sortBy,
            @RequestParam(required = false) LocalDate startDate,
            @RequestParam(required = false) LocalDate endDate) {
        return ResponseEntity.ok().body(newsfeedService.getNewsfeeds(page, sortBy, startDate, endDate));
    }

    @Operation(summary = "getNewsfeed", description = "뉴스피드 선택조회 기능입니다.")
    @GetMapping("/{newsfeedSeq}")
    public ResponseEntity<NewsfeedResponseDto> getNewsfeed(@PathVariable("newsfeedSeq") Long newsfeedSeq) {
        return ResponseEntity.ok().body(newsfeedService.getNewsfeed(newsfeedSeq));
    }

    @Operation(summary = "updateNewsfeed", description = "뉴스피드 수정 기능입니다.")
    @PutMapping("/{newsfeedSeq}")
    private ResponseEntity<NewsfeedResponseDto> updateNewsfeed(@PathVariable("newsfeedSeq") Long newsfeedSeq, @Valid @RequestBody NewsfeedRequestDto requestDto, @AuthenticationPrincipal UserDetailsImpl userDetails){
        return ResponseEntity.ok().body(newsfeedService.updateNewsFeed(newsfeedSeq,requestDto,userDetails.getUser()));
    }

    @Operation(summary = "deleteNewsfeed", description = "뉴스피드 삭제 기능입니다.")
    @DeleteMapping("/{newsfeedSeq}")
    private ResponseEntity<String> deleteNewsfeed(@PathVariable("newsfeedSeq") Long newsfeedSeq, @AuthenticationPrincipal UserDetailsImpl userDetails){
        newsfeedService.deleteNewsFeed(newsfeedSeq,userDetails.getUser());
        return ResponseEntity.noContent().build();
    }

}
