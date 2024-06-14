package com.sparta.newspeed.domain.comment.entity;

import com.sparta.newspeed.domain.comment.dto.CommentRequestDto;
import com.sparta.newspeed.common.Timestamped;
import com.sparta.newspeed.domain.newsfeed.entity.Newsfeed;
import com.sparta.newspeed.domain.user.entity.User;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Table(name = "comments")
public class Comment extends Timestamped {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "comment_seq")
    private Long commentSeq;

    @NotBlank
    @Column(name = "content")
    private String content;

    @Column(name = "likes")
    private Long like;

    @ManyToOne
    @JoinColumn(name = "user_seq")
    private User user;

    @ManyToOne
    @JoinColumn(name = "newsfeed_seq")
    private Newsfeed newsfeed;

    public void update(CommentRequestDto requestDto) {
        this.content = requestDto.getContent();
    }

    public void increaseLike() { this.like++; }

    public void decreaseLike() { this.like--; }
}