package com.sparta.newspeed.domain.likes.repository;

import com.sparta.newspeed.domain.likes.entity.Like;
import com.sparta.newspeed.domain.likes.entity.LikeEnum;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface LikeRepository extends JpaRepository<Like, Long> {
    // 유저 ID , contents ID, contents type으로 해당 좋아요가 있는지 확인
    Optional<Like> findByUserIdAndContentIdAndContentType(Long userId, Long contentId, LikeEnum contentType);
    // 해당 ID를 가진 CONTENTS type이 몇개 있는지 확인
    int countByContentIdAndContentType(Long newsfeedId, LikeEnum contentType);

}
