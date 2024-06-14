package com.sparta.newspeed.domain.likes.controller;

import com.sparta.newspeed.domain.likes.dto.LikeCountResponseDto;
import com.sparta.newspeed.domain.likes.entity.LikeEnum;
import com.sparta.newspeed.domain.likes.service.LikeService;
import com.sparta.newspeed.security.service.UserDetailsImpl;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@Tag(name = "Likes API", description = "Likes API 입니다")
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/newsfeeds")
public class LikeController {

    private final LikeService likeService;

    // 게시물별 좋아요 수 조회
    @Operation(summary = "getNewsfeedsLikes", description = "뉴스피드별 좋아요 수 조회 기능입니다.")
    @GetMapping("/{newsfeedId}/like/count")
    public LikeCountResponseDto getNewsfeedsLikes(@PathVariable("newsfeedId") Long newsfeedId) {
        return new LikeCountResponseDto(likeService.getLikesCount(newsfeedId, LikeEnum.NEWSFEED));
    }

    // 댓글별 좋아요 수 조회
    @Operation(summary = "getCommentsLikes", description = "댓글별 좋아요 수 조회 기능입니다.")
    @GetMapping("/{newsfeedId}/comments/{commentsId}/like/count")
    public LikeCountResponseDto getCommentsLikes(@PathVariable("commentsId") Long commentsId) {
        return new LikeCountResponseDto(likeService.getLikesCount(commentsId, LikeEnum.COMMENT));
    }

    @Operation(summary = "toggleNewsfeedLike", description = "게시물 좋아요 추가 및 삭제 기능입니다.")
    @PostMapping("/{newsfeedId}/like")
    public ResponseEntity<String> toggleNewsfeedLike(@PathVariable(name = "newsfeedId") long newsfeedId, @AuthenticationPrincipal UserDetailsImpl userDetails) {
        return likeService.toggleNewsfeedLike(userDetails.getUser().getUserSeq(), newsfeedId);
    }

    @Operation(summary = "toggleCommentLike", description = "댓글 좋아요 추가 및 삭제 기능입니다.")
    @PostMapping("/{newsfeedId}/comments/{commentId}/like")
    public ResponseEntity<String> toggleCommentLike(@PathVariable(name = "commentId") long commentId, @AuthenticationPrincipal UserDetailsImpl userDetails) {
        return likeService.toggleCommentLike(userDetails.getUser().getUserSeq(), commentId);
    }
}
