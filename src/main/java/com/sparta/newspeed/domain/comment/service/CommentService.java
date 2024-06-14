package com.sparta.newspeed.domain.comment.service;

import com.sparta.newspeed.domain.comment.dto.CommentRequestDto;
import com.sparta.newspeed.domain.comment.dto.CommentResponseDto;
import com.sparta.newspeed.domain.comment.entity.Comment;
import com.sparta.newspeed.domain.comment.repository.CommentRepository;
import com.sparta.newspeed.common.exception.CustomException;
import com.sparta.newspeed.common.exception.ErrorCode;
import com.sparta.newspeed.domain.newsfeed.entity.Newsfeed;
import com.sparta.newspeed.domain.newsfeed.service.NewsfeedService;
import com.sparta.newspeed.domain.user.entity.User;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Objects;

@Service
@RequiredArgsConstructor
public class CommentService {

    private final NewsfeedService newsfeedService;
    private final CommentRepository commentRepository;

    public CommentResponseDto createComment(
            long newsfeedSeq,
            CommentRequestDto requestDto,
            User user) {

        // DB에 존재하는 Newsfeed
        Newsfeed newsfeed = newsfeedService.findNewsfeed(newsfeedSeq);

        // Entity 생성
        Comment comment = Comment.builder()
                .content(requestDto.getContent())
                .user(user)
                .newsfeed(newsfeed)
                .like(0L)
                .build();

        // DB에 저장
        Comment savedComment = commentRepository.save(comment);

        return new CommentResponseDto(savedComment);
    }

    public List<CommentResponseDto> findAll(long newsfeedSeq) {
        // DB에 존재하는 Newsfeed
        Newsfeed newsfeed = newsfeedService.findNewsfeed(newsfeedSeq);

        // DB에 존재하는 comment list
        List<Comment> commentList = commentRepository.findByNewsfeedNewsFeedSeq(newsfeedSeq);

        // 댓글이 없는 경우 예외 처리
        if (commentList.isEmpty()) {
            throw new CustomException(ErrorCode.COMMENT_NOT_FOUND);
        }

        return commentList.stream()
                .map(CommentResponseDto::new)
                .toList();
    }

    @Transactional
    public CommentResponseDto updateComment(
            long newsfeedSeq,
            long commentSeq,
            CommentRequestDto requestDto,
            User user) {

        // DB에 존재하는 Newsfeed
        Newsfeed newsfeed = newsfeedService.findNewsfeed(newsfeedSeq);

        // DB에 존재하는 Comment
        Comment comment = findComment(commentSeq);

        // 사용자 일치 여부 판별
        if (!Objects.equals(comment.getUser().getUserName(), user.getUserName())) {
            throw new CustomException(ErrorCode.COMMENT_NOT_USER);
        }

        // Comment 수정
        comment.update(requestDto);

        return new CommentResponseDto(comment);
    }

    public void deleteComment(
            long newsfeedSeq,
            long commentSeq,
            User user) {

        // DB에 존재하는 Newsfeed
        Newsfeed newsfeed = newsfeedService.findNewsfeed(newsfeedSeq);

        // DB에 존재하는 Comment
        Comment comment = findComment(commentSeq);

        // 사용자 일치 여부 판별
        if (!Objects.equals(comment.getUser().getUserName(), user.getUserName())) {
            throw new CustomException(ErrorCode.COMMENT_NOT_USER);
        }

        // Comment 삭제
        commentRepository.delete(comment);
    }

    // 좋아요 증감 함수
    @Transactional
    public void increaseCommentLike(Long commentSeq){
        Comment comment = findComment(commentSeq);
        comment.increaseLike();
    }

    @Transactional
    public void decreaseCommentLike(Long commentSeq){
        Comment comment =  findComment(commentSeq);
        comment.decreaseLike();
    }

    private Comment findComment(Long id) {
        return commentRepository.findById(id).orElseThrow(() ->
                new CustomException(ErrorCode.COMMENT_NOT_FOUND)
        );
    }

    public void validateCommentLike(Long userId, Long commentSeq) {
        Comment comment = commentRepository.findById(commentSeq).orElseThrow(() ->
                new CustomException(ErrorCode.COMMENT_NOT_FOUND));
        if (comment.getUser().getUserSeq().equals(userId)) {
            throw new CustomException(ErrorCode.COMMENT_SAME_USER);
        }
    }
}