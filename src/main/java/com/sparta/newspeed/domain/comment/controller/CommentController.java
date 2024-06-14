package com.sparta.newspeed.domain.comment.controller;

import com.sparta.newspeed.domain.comment.dto.CommentRequestDto;
import com.sparta.newspeed.domain.comment.dto.CommentResponseDto;
import com.sparta.newspeed.domain.comment.service.CommentService;
import com.sparta.newspeed.security.service.UserDetailsImpl;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = "Comment API", description = "Comment API")
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/newsfeeds")
public class CommentController {

    private final CommentService commentService;

    @Operation(summary = "create comment", description = "새로운 댓글 생성 기능")
    @PostMapping("/{newsfeedId}/comments")
    public ResponseEntity<CommentResponseDto> createComment(
            @PathVariable(name = "newsfeedId") long newsfeedId,
            @RequestBody @Valid CommentRequestDto requestDto,
            @AuthenticationPrincipal UserDetailsImpl userDetails) {

        return ResponseEntity.status(HttpStatus.CREATED).body(commentService.createComment(newsfeedId, requestDto, userDetails.getUser()));
    }

    @Operation(summary = "get comments", description = "댓글 전체조회 기능")
    @GetMapping("/{newsfeedId}/comments")
    public ResponseEntity<List<CommentResponseDto>> getComments(
            @PathVariable(name = "newsfeedId") long newsfeedId) {

        return ResponseEntity.ok().body(commentService.findAll(newsfeedId));
    }

    @Operation(summary = "update comment", description = "댓글 수정 기능")
    @PutMapping("/{newsfeedId}/comments/{commentId}")
    public ResponseEntity<CommentResponseDto> updateComment(
            @PathVariable(name = "newsfeedId") long newsfeedId,
            @PathVariable(name = "commentId") long commentId,
            @RequestBody @Valid CommentRequestDto requestDto,
            @AuthenticationPrincipal UserDetailsImpl userDetails) {

        return ResponseEntity.ok().body(commentService.updateComment(newsfeedId, commentId, requestDto, userDetails.getUser()));
    }

    @Operation(summary = "delete comment", description = "댓글 삭제 기능")
    @DeleteMapping("/{newsfeedId}/comments/{commentId}")
    public ResponseEntity<Void> deleteComment (
            @PathVariable(name = "newsfeedId") long newsfeedId,
            @PathVariable(name = "commentId") long commentId,
            @AuthenticationPrincipal UserDetailsImpl userDetails) {

        commentService.deleteComment(newsfeedId, commentId, userDetails.getUser());
        return ResponseEntity.noContent().build();
    }
}