package com.sparta.newspeed.domain.newsfeed.service;

import com.sparta.fixturemonkey.FixtureMonkeyUtil;
import com.sparta.newspeed.NewsfeedApplicationTests;
import com.sparta.newspeed.common.exception.CustomException;
import com.sparta.newspeed.common.exception.ErrorCode;
import com.sparta.newspeed.domain.newsfeed.dto.NewsfeedRequestDto;
import com.sparta.newspeed.domain.newsfeed.dto.NewsfeedResponseDto;
import com.sparta.newspeed.domain.newsfeed.entity.Newsfeed;
import com.sparta.newspeed.domain.newsfeed.entity.Ott;
import com.sparta.newspeed.domain.newsfeed.repository.NewsfeedRespository;
import com.sparta.newspeed.domain.newsfeed.repository.OttRepository;
import com.sparta.newspeed.domain.user.entity.User;
import net.jqwik.api.Arbitraries;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.SpyBean;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.BDDMockito.*;

@DisplayName("뉴스피드 서비스 테스트")
class NewsfeedServiceTest extends NewsfeedApplicationTests {
    private List<Newsfeed> newsfeeds;

    @Autowired
    NewsfeedService newsfeedService;

    @Autowired
    NewsfeedRespository newsfeedRespository;

    @SpyBean
    OttRepository ottRepository;

    @BeforeEach
    void setUp() {
        newsfeeds = newsfeedRespository.saveAll(getNewsfeedDataInit(100));
    }

    String title = FixtureMonkeyUtil.getRandomStringArbitrary(5, 20).sample();
    String content = FixtureMonkeyUtil.getRandomStringArbitrary(5, 20).sample();
    String ottName = Arbitraries.of("Netflix", "Disney+", "watcha", "wavve", "tiving").sample();
    int remainMember = Arbitraries.integers().between(1, 4).sample();

    @Nested
    @DisplayName("뉴스피드 게시물 생성")
    class Create {

        @DisplayName("뉴스피드 게시물 생성 - 정상 동작")
        @Transactional
        @Test
        void test1() {
            // given
            User user = newsfeeds.get(0).getUser();

            NewsfeedRequestDto requestDto = FixtureMonkeyUtil.monkey()
                    .giveMeBuilder(NewsfeedRequestDto.class)
                    .set("title", title)
                    .set("content", content)
                    .set("ottName", ottName)
                    .set("remainMember", remainMember)
                    .sample();

            // when
            NewsfeedResponseDto responseDto  = newsfeedService.createNewsFeed(requestDto, user);

            // then
            Newsfeed createNewsFeed= newsfeedRespository.findById(responseDto.getNewsFeedSeq()).orElse(null);

            assertEquals(requestDto.getTitle(), createNewsFeed.getTitle());
            assertEquals(requestDto.getContent(), createNewsFeed.getContent());
            assertEquals(requestDto.getOttName(), createNewsFeed.getOtt().getOttName());
            assertEquals(requestDto.getRemainMember(), createNewsFeed.getRemainMember());
            assertEquals(user.getUserName(), createNewsFeed.getUser().getUserName());
        }

        @DisplayName("뉴스피드 게시물 생성 - 남은 인원 수 전체 인원수 초과")
        @Transactional
        @Test
        void test2() {
            // given
            User user = newsfeeds.get(0).getUser();

            remainMember = 5;

            NewsfeedRequestDto requestDto = FixtureMonkeyUtil.monkey()
                    .giveMeBuilder(NewsfeedRequestDto.class)
                    .set("title", title)
                    .set("content", content)
                    .set("ottName", ottName)
                    .set("remainMember", remainMember)
                    .sample();

            // when
            CustomException exception = assertThrows(CustomException.class,
                    () -> newsfeedService.createNewsFeed(requestDto, user));

            // then
            assertEquals(exception.getErrorCode(), ErrorCode.NEWSFEED_REMAIN_MEMBER_OVER);
        }
    }

    @Nested
    @DisplayName("뉴스피드 게시물 조회")
    class Read {
        // given
        int page = 0;
        String sortBy = "createAt";
        LocalDate startDate = LocalDate.now();
        LocalDate endDate = LocalDateTime.now().toLocalDate().plusDays(1);

        @DisplayName("뉴스피드 게시물 전체 조회")
        @Test
        void test1() {
            // when
            List<NewsfeedResponseDto> responseDto = newsfeedService.getNewsfeeds(page, sortBy, null, null);

            // then
            // 정렬된 데이터 조회는 뭘로 테스트하나?
            List<NewsfeedResponseDto> sortedNewsFeeds = newsfeeds.stream()
                    .sorted(Comparator.comparing(Newsfeed::getCreatedAt).reversed())
                    .map(NewsfeedResponseDto::new)
                    .toList();

            assertEquals(sortedNewsFeeds.get(0).getTitle(), responseDto.get(0).getTitle());
        }

        @DisplayName("뉴스피드 게시물 전체 조회 - 해당하는 페이지의 데이터 미존재")
        @Test
        void test2() {
            // when
            CustomException exception1 = assertThrows(CustomException.class,
                    () -> newsfeedService.getNewsfeeds(100000, sortBy, startDate, endDate));

            CustomException exception2 = assertThrows(CustomException.class,
                    () -> newsfeedService.getNewsfeeds(100000, sortBy, null, null));

            // then
            assertEquals(exception1.getErrorCode(), ErrorCode.NEWSFEED_PERIOD_EMPTY);
            assertEquals(exception2.getErrorCode(), ErrorCode.NEWSFEED_EMPTY);
        }


        @DisplayName("뉴스피드 게시물 전체 조회 - 기간 입력 누락")
        @Test
        void test3() {
            // when
            CustomException exception1 = assertThrows(CustomException.class,
                    () -> newsfeedService.getNewsfeeds(page, sortBy, startDate, null));

            CustomException exception2 = assertThrows(CustomException.class,
                    () -> newsfeedService.getNewsfeeds(page, sortBy, null, endDate));

            // then
            assertEquals(exception1.getErrorCode(), ErrorCode.MISSING_PERIOD_INPUT);
            assertEquals(exception2.getErrorCode(), ErrorCode.MISSING_PERIOD_INPUT);
        }

        @DisplayName("뉴스피드 게시물 선택 조회")
        @Test
        void test4() {
            // given
            Newsfeed newsfeed = newsfeeds.get(0);
            Long newsFeedSeq = newsfeed.getNewsFeedSeq();

            // when
            NewsfeedResponseDto responseDto = newsfeedService.getNewsfeed(newsFeedSeq);

            // then
            assertEquals(newsFeedSeq, responseDto.getNewsFeedSeq());
            assertEquals(newsfeed.getTitle(), responseDto.getTitle());
            assertEquals(newsfeed.getContent(), responseDto.getContent());
            assertEquals(newsfeed.getOtt().getOttName(), responseDto.getOttName());
            assertEquals(newsfeed.getRemainMember(), responseDto.getRemainMember());
            assertEquals(newsfeed.getUser().getUserName(), responseDto.getUserName());
        }
    }

    @Nested
    @DisplayName("뉴스피드 게시물 업데이트")
    class Update {
        Long newsFeedSeq;
        User user;
        NewsfeedRequestDto requestDto;

        @BeforeEach
        void setUp() {
            // given
            newsFeedSeq = newsfeeds.get(0).getNewsFeedSeq();
            user = newsfeeds.get(0).getUser();
        }

        @DisplayName("뉴스피드 게시물 업데이트")
        @Test
        void test1() {
            // given
            NewsfeedRequestDto requestDto = FixtureMonkeyUtil.monkey()
                    .giveMeBuilder(NewsfeedRequestDto.class)
                    .set("title", title)
                    .set("content", content)
                    .set("ottName", ottName)
                    .set("remainMember", remainMember)
                    .sample();

            // when
            NewsfeedResponseDto responseDto = newsfeedService.updateNewsFeed(newsFeedSeq, requestDto, user);

            // then
            assertEquals(title, requestDto.getTitle());
            assertEquals(content, requestDto.getContent());
            assertEquals(ottName, requestDto.getOttName());
            assertEquals(remainMember, requestDto.getRemainMember());
        }

        @DisplayName("뉴스피드 게시물 업데이트 - 남은 인원 수 전체 인원수 초과")
        @Test
        void test2() {
            // given
            NewsfeedRequestDto requestDto = FixtureMonkeyUtil.monkey()
                    .giveMeBuilder(NewsfeedRequestDto.class)
                    .set("title", title)
                    .set("content", content)
                    .set("ottName", ottName)
                    .set("remainMember", Arbitraries.integers().between(5, 1000))
                    .sample();

            // when
            CustomException exception = assertThrows(CustomException.class,
                    () -> newsfeedService.updateNewsFeed(newsFeedSeq, requestDto, user));

            // then
            assertEquals(exception.getErrorCode(), ErrorCode.NEWSFEED_REMAIN_MEMBER_OVER);
        }
    }

    @DisplayName("뉴스피드 게시물 삭제")
    @Test
    public void test1() {
        // given
        Long newsFeedSeq = newsfeeds.get(0).getNewsFeedSeq();
        User user = newsfeeds.get(0).getUser();

        // when
        newsfeedService.deleteNewsFeed(newsFeedSeq, user);

        // then
        CustomException exception = assertThrows(CustomException.class,
                () -> newsfeedService.findNewsfeed(newsFeedSeq));

        assertEquals(exception.getErrorCode(), ErrorCode.NEWSFEED_NOT_FOUND);
    }

    @DisplayName("뉴스피드 게시물 좋아요 추가")
    @Test
    public void test2() {
        // given
        Long newsFeedSeq = newsfeeds.get(0).getNewsFeedSeq();
        Long likeCount = newsfeeds.get(0).getLike();

        // when
        newsfeedService.increaseNewsfeedLike(newsFeedSeq);

        // then
        Newsfeed newsfeed = newsfeedService.findNewsfeed(newsFeedSeq);
        assertEquals(likeCount + 1, newsfeed.getLike());
    }

    @DisplayName("뉴스피드 게시물 좋아요 삭제")
    @Test
    public void test3() {
        // given
        Long newsFeedSeq = newsfeeds.get(0).getNewsFeedSeq();
        Long likeCount = newsfeeds.get(0).getLike();

        // when
        newsfeedService.decreaseNewsfeedLike(newsFeedSeq);

        // then
        Newsfeed newsfeed = newsfeedService.findNewsfeed(newsFeedSeq);
        assertEquals(likeCount - 1, newsfeed.getLike());
    }
 }
