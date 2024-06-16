package com.sparta.newspeed.domain.comment.service;

import com.sparta.fixturemonkey.FixtureMonkeyUtil;
import com.sparta.newspeed.NewsfeedApplicationTests;
import com.sparta.newspeed.common.exception.CustomException;
import com.sparta.newspeed.common.exception.ErrorCode;
import com.sparta.newspeed.domain.comment.dto.CommentRequestDto;
import com.sparta.newspeed.domain.comment.dto.CommentResponseDto;
import com.sparta.newspeed.domain.comment.entity.Comment;
import com.sparta.newspeed.domain.comment.repository.CommentRepository;
import com.sparta.newspeed.domain.newsfeed.entity.Newsfeed;
import com.sparta.newspeed.domain.newsfeed.repository.NewsfeedRespository;
import com.sparta.newspeed.domain.user.entity.User;
import com.sparta.newspeed.domain.user.entity.UserRoleEnum;
import com.sparta.newspeed.domain.user.repository.UserRepository;
import net.jqwik.api.Arbitraries;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("댓글 서비스 테스트")
class CommentServiceTest extends NewsfeedApplicationTests {
    private List<Comment> comments;
    private Long newsfeedSeq;

    private String content = FixtureMonkeyUtil.getRandomStringArbitrary(5, 20).sample();

    @Autowired
    CommentService commentService;

    @Autowired
    CommentRepository commentRepository;

    @Autowired
    NewsfeedRespository newsfeedRespository;

    @Autowired
    UserRepository userRepository;

    @BeforeEach
    void setUp() {
        int count = Arbitraries.integers().between(10, 50).sample();
        List<Newsfeed> newsfeeds = newsfeedRespository.saveAll(getNewsfeedDataInit(10));
        comments = commentRepository.saveAll(getCommentDataInit(count, newsfeeds));
        newsfeedSeq = comments.get(0).getNewsfeed().getNewsFeedSeq();
    }

    @DisplayName("댓글 생성")
    @Test
    void test1() {
        // given
        User user = getUser();

        CommentRequestDto requestDto = FixtureMonkeyUtil.monkey()
                .giveMeBuilder(CommentRequestDto.class)
                .set("content", content)
                .sample();

        // when
        CommentResponseDto responseDto = commentService.createComment(newsfeedSeq, requestDto, user);

        // then
        assertEquals(requestDto.getContent(), responseDto.getContent());
    }

    @DisplayName("댓글 조회")
    @Test
    void test2() {
        // when
        List<CommentResponseDto> responseDto = commentService.findAll(newsfeedSeq);

        // then
        assertNotNull(responseDto);
    }

    @DisplayName("댓글 조회 - 미존재 댓글")
    @Test
    void test3() {
        // given
        commentRepository.deleteAll();

        // when
        CustomException exception = assertThrows(CustomException.class,
                () -> commentService.findAll(newsfeedSeq));

        // then
        assertEquals(exception.getErrorCode(), ErrorCode.COMMENT_NOT_FOUND);
    }

    @DisplayName("댓글 수정")
    @Test
    void test4() {
        // given
        Long commentSeq = comments.get(0).getCommentSeq();

        User user = comments.get(0).getUser();

        CommentRequestDto requestDto = FixtureMonkeyUtil.monkey()
                .giveMeBuilder(CommentRequestDto.class)
                .set("content", content)
                .sample();

        // when
        CommentResponseDto responseDto = commentService.updateComment(newsfeedSeq, commentSeq, requestDto, user);

        // then
        assertEquals(requestDto.getContent(), responseDto.getContent());
    }

    @DisplayName("댓글 수정 - 사용자가 일치하지 않음")
    @Test
    void test5() {
        // given
        Long commentSeq = comments.get(0).getCommentSeq();

        User user = userRepository.save(
                User.builder()
                        .userId("test")
                        .userPassword("1")
                        .userName("1")
                        .userEmail("test1@test.com")
                        .role(UserRoleEnum.USER)
                        .build()
        );

        CommentRequestDto requestDto = FixtureMonkeyUtil.monkey()
                .giveMeBuilder(CommentRequestDto.class)
                .set("content", content)
                .sample();

        // when
        CustomException exception = assertThrows(CustomException.class,
                () -> commentService.updateComment(newsfeedSeq, commentSeq, requestDto, user));

        // then
        assertEquals(exception.getErrorCode(), ErrorCode.COMMENT_NOT_USER);
    }

    @DisplayName("댓글 삭제")
    @Test
    void test6() {
        // given
        Long commentSeq = comments.get(0).getCommentSeq();
        User user = comments.get(0).getUser();

        // when
        commentService.deleteComment(newsfeedSeq, commentSeq, user);

        // then
        assertNull(commentRepository.findById(commentSeq).orElse(null));
    }

    @DisplayName("댓글 좋아요 추가")
    @Test
    void test7() {
        // given
        Long commentSeq = comments.get(0).getCommentSeq();
        Long likeCount = comments.get(0).getLike();

        // when
        commentService.increaseCommentLike(commentSeq);

        // then
        Comment comment = commentRepository.findById(commentSeq).orElse(null);
        assertEquals(likeCount + 1, comment.getLike());
    }

    @DisplayName("댓글 좋아요 추가")
    @Test
    void test8() {
        // given
        Long commentSeq = comments.get(0).getCommentSeq();
        Long likeCount = comments.get(0).getLike();

        // when
        commentService.decreaseCommentLike(commentSeq);

        // then
        Comment comment = commentRepository.findById(commentSeq).orElse(null);
        assertEquals(likeCount - 1, comment.getLike());
    }
}