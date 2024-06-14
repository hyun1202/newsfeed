package com.sparta.newspeed.domain.likes.service;

import com.sparta.newspeed.domain.comment.service.CommentService;
import com.sparta.newspeed.domain.likes.repository.LikeRepository;
import com.sparta.newspeed.domain.likes.entity.Like;
import com.sparta.newspeed.domain.likes.entity.LikeEnum;
import com.sparta.newspeed.domain.newsfeed.service.NewsfeedService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class LikeService {


    private final LikeRepository likeRepository;
    private final NewsfeedService newsfeedService;
    private final CommentService commentService;


    // 게시물, 댓글별 좋아요 수 count
    public int getLikesCount(Long contentId, LikeEnum contentType) {
        return likeRepository.countByContentIdAndContentType(contentId, contentType);
    }

    // 게시물별 좋아요 toggle 기능
    public ResponseEntity<String> toggleNewsfeedLike(Long userId, Long newsfeedId) {
        validateNewsfeedLike(userId, newsfeedId);

        Optional<Like> likeOptional = findLike(userId, newsfeedId, LikeEnum.NEWSFEED);
        if (likeOptional.isPresent()) {
            likeRepository.delete(likeOptional.get());
            newsfeedService.decreaseNewsfeedLike(newsfeedId);
            return ResponseEntity.ok("Like removed");
        } else {
            Like like = new Like(userId, newsfeedId, LikeEnum.NEWSFEED);
            likeRepository.save(like);
            newsfeedService.increaseNewsfeedLike(newsfeedId);
            return ResponseEntity.ok("Like added");
        }
    }

    // 댓글별 좋아요 toggle 기능
    public ResponseEntity<String> toggleCommentLike(Long userId, Long commentId) {
        validateCommentLike(userId, commentId);

        Optional<Like> likeOptional = findLike(userId, commentId, LikeEnum.COMMENT);
        if (likeOptional.isPresent()) {
            likeRepository.delete(likeOptional.get());
            commentService.decreaseCommentLike(commentId);
            return ResponseEntity.ok("Like removed");
        } else {
            Like like = new Like(userId, commentId, LikeEnum.COMMENT);
            likeRepository.save(like);
            commentService.increaseCommentLike(commentId);
            return ResponseEntity.ok("Like added");
        }
    }

    // 유효성 검사
    private void validateNewsfeedLike(Long userId, Long newsfeedId) {
        newsfeedService.validateNewsfeedLike(userId, newsfeedId);
    }

    private void validateCommentLike(Long userId, Long commentId) {
        commentService.validateCommentLike(userId, commentId);
    }


    // 좋아요 객체 찾기
    private Optional<Like> findLike(Long userId, Long contentId, LikeEnum contentType) {
        return likeRepository.findByUserIdAndContentIdAndContentType(userId, contentId, contentType);
    }

}
