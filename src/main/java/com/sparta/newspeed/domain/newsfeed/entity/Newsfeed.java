package com.sparta.newspeed.domain.newsfeed.entity;

import com.sparta.newspeed.common.Timestamped;
import com.sparta.newspeed.domain.newsfeed.dto.NewsfeedRequestDto;
import com.sparta.newspeed.domain.user.entity.User;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
@Table(name = "newsfeed")
public class Newsfeed extends Timestamped {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "newsfeed_seq")
    private Long newsFeedSeq;

    @Column(name = "title")
    private String title;

    @Column(name = "content")
    private String content;

    @Column(name = "remain_member")
    private int remainMember;

    @Column(name = "likes")
    private Long like;

    @ManyToOne
    @JoinColumn(name = "user_seq")
    private User user;

    @ManyToOne
    @JoinColumn(name = "ott_seq")
    private Ott ott;

    public void updateNewsfeed(NewsfeedRequestDto request, Ott ott) {
        this.title = request.getTitle();
        this.content = request.getContent();
        this.remainMember = request.getRemainMember();
        this.ott = ott;
    }

    public void increaseLike() { this.like++; }

    public void decreaseLike() { this.like--; }
}
