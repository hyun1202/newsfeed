package com.sparta.newspeed.domain.comment.entity;

import com.sparta.fixturemonkey.FixtureMonkeyUtil;
import com.sparta.newspeed.domain.comment.dto.CommentRequestDto;
import com.sparta.newspeed.domain.newsfeed.dto.NewsfeedRequestDto;
import com.sparta.newspeed.domain.newsfeed.entity.Newsfeed;
import net.jqwik.api.Arbitraries;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class CommentTest {

    Comment comment;

    @BeforeEach
    void setUp() {
        comment = FixtureMonkeyUtil.Entity.toComment();
    }

    @DisplayName("뉴스피드 댓글 업데이트")
    @Test
    void test1() {
        // given
        String content = "updateContent";

        CommentRequestDto requestDto = FixtureMonkeyUtil.monkey()
                .giveMeBuilder(CommentRequestDto.class)
                .set("content", content)
                .sample();

        // when
        comment.update(requestDto);

        // then
        assertEquals(requestDto.getContent(), comment.getContent());
    }

    @DisplayName("뉴스피드 댓글 좋아요 추가 테스트")
    @Test
    void test2() {
        // given
        Long likeCount = 5L;

        comment = FixtureMonkeyUtil.monkey()
                .giveMeBuilder(Comment.class)
                .set("like", likeCount)
                .sample();

        // when
        comment.increaseLike();

        // then
        assertEquals(++likeCount, comment.getLike());
    }

    @DisplayName("뉴스피드 댓글 좋아요 삭제 테스트")
    @Test
    void test3() {
        // given
        Long likeCount = 5L;

        comment = FixtureMonkeyUtil.monkey()
                .giveMeBuilder(Comment.class)
                .set("like", likeCount)
                .sample();

        // when
        comment.decreaseLike();

        // then
        assertEquals(--likeCount, comment.getLike());
    }
}