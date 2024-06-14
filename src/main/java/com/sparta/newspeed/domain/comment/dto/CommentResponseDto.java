package com.sparta.newspeed.domain.comment.dto;

import com.sparta.newspeed.domain.comment.entity.Comment;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@AllArgsConstructor
@Builder
public class CommentResponseDto {

    private String userName;
    private String content;
    private LocalDateTime createdAt;
    private LocalDateTime modifiedAt;

    public CommentResponseDto(Comment comment) {
        this.userName = comment.getUser().getUserName();
        this.content = comment.getContent();
        this.createdAt = comment.getCreatedAt();
        this.modifiedAt = comment.getModifiedAt();
    }
}