package com.sparta.newspeed.domain.newsfeed.entity;

import com.sparta.fixturemonkey.FixtureMonkeyUtil;
import com.sparta.newspeed.domain.newsfeed.dto.NewsfeedRequestDto;
import net.jqwik.api.Arbitraries;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class NewsfeedTest {

    Newsfeed newsFeed;
    Ott ott;

    @BeforeEach
    void setUp() {
        newsFeed = FixtureMonkeyUtil.Entity.toNewsfeed();
        ott = new Ott("watcha", 1_000, 4);
    }

    @DisplayName("뉴스피드 게시글 업데이트")
    @Test
    void test1() {
        // given
        String title = "updateTitle";
        String content = "updateContent";
        int remainMember = 4;
        String ottName = "watcha";

        NewsfeedRequestDto requestDto = FixtureMonkeyUtil.monkey()
                .giveMeBuilder(NewsfeedRequestDto.class)
                .set("title", title)
                .set("content", content)
                .set("ottName", ottName)
                .set("remainMember", remainMember)
                .sample();
        // when
        newsFeed.updateNewsfeed(requestDto, ott);

        // then
        assertEquals(requestDto.getTitle(), newsFeed.getTitle());
        assertEquals(requestDto.getContent(), newsFeed.getContent());
        assertEquals(requestDto.getOttName(), newsFeed.getOtt().getOttName());
        assertEquals(requestDto.getRemainMember(), newsFeed.getRemainMember());
    }

    @DisplayName("뉴스피드 게시글 좋아요 추가 테스트")
    @Test
    void test2() {
        // given
        Long likeCount = 5L;

        newsFeed = FixtureMonkeyUtil.monkey()
                .giveMeBuilder(Newsfeed.class)
                .set("like", likeCount)
                .sample();

        // when
        newsFeed.increaseLike();

        // then
        assertEquals(++likeCount, newsFeed.getLike());
    }

    @DisplayName("뉴스피드 게시글 좋아요 삭제 테스트")
    @Test
    void test3() {
        // given
        Long likeCount = 5L;

        newsFeed = FixtureMonkeyUtil.monkey()
                .giveMeBuilder(Newsfeed.class)
                .set("like", likeCount)
                .sample();

        // when
        newsFeed.decreaseLike();

        // then
        assertEquals(--likeCount, newsFeed.getLike());
    }
}