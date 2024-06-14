package com.sparta.newspeed.domain.likes.entity;

import com.sparta.newspeed.common.Timestamped;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "likes")
public class Like extends Timestamped {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "likes_seq")
    private Long likesSeq;

    @Column(name = "user_id")
    private Long userId;

    @Column(name = "content_id", nullable = false)
    private Long contentId;

    @Column(name = "content_type", nullable = false)
    @Enumerated(value = EnumType.STRING)
    private LikeEnum contentType;


    public Like(Long userId, Long contentId, LikeEnum contentType) {
        this.userId = userId;
        this.contentId = contentId;
        this.contentType = contentType;
    }
}
