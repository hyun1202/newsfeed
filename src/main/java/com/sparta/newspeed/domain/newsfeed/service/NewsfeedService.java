package com.sparta.newspeed.domain.newsfeed.service;

import com.sparta.newspeed.common.exception.CustomException;
import com.sparta.newspeed.common.exception.ErrorCode;
import com.sparta.newspeed.domain.newsfeed.dto.NewsfeedRequestDto;
import com.sparta.newspeed.domain.newsfeed.dto.NewsfeedResponseDto;
import com.sparta.newspeed.domain.newsfeed.repository.NewsfeedRespository;
import com.sparta.newspeed.domain.newsfeed.repository.OttRepository;
import com.sparta.newspeed.domain.newsfeed.entity.Newsfeed;
import com.sparta.newspeed.domain.newsfeed.entity.Ott;
import com.sparta.newspeed.domain.user.entity.User;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;

@Service
@RequiredArgsConstructor
public class NewsfeedService {

    private final NewsfeedRespository newsfeedRespository;
    private final OttRepository ottRepository;

    private static final int PAGE_SIZE = 10;

    public List<NewsfeedResponseDto> getNewsfeeds(int page, String sortBy, LocalDate startDate, LocalDate endDate) {
        Pageable pageable = PageRequest.of(page, PAGE_SIZE, getSortBy(sortBy));

        if (startDate != null && endDate != null) {
            // 날짜 범위를 입력 받았을 경우, 기간별 조회
            Page<Newsfeed> newsfeedPage = newsfeedRespository.findByCreatedAtBetween(startDate.atStartOfDay(), endDate.plusDays(1).atStartOfDay(), pageable);
            if (newsfeedPage.isEmpty()) {
                throw new CustomException(ErrorCode.NEWSFEED_PERIOD_EMPTY);
            }
            return newsfeedPage.getContent().stream()
                    .map(NewsfeedResponseDto::new)
                    .toList();
        } else if (startDate == null && endDate == null) {
            // 날짜 범위를 전부 입력 받지 않았을 경우, 전체 조회
            Page<Newsfeed> newsfeedPage = newsfeedRespository.findAll(pageable);
            if (newsfeedPage.isEmpty()) {
                throw new CustomException(ErrorCode.NEWSFEED_EMPTY);
            }
            return newsfeedPage.getContent().stream()
                    .map(NewsfeedResponseDto::new)
                    .toList();
        } else {
            // 기간 입력이 누락되었을 경우, 예외 처리
            throw new CustomException(ErrorCode.MISSING_PERIOD_INPUT);
        }
    }

    public NewsfeedResponseDto getNewsfeed(Long newsfeedSeq) {
        Newsfeed newsfeed = findNewsfeed(newsfeedSeq);
        return new NewsfeedResponseDto(newsfeed);
    }

    public NewsfeedResponseDto createNewsFeed(NewsfeedRequestDto request, User user) {
        Ott ott = findOtt(request);
        if (!isRemainMembersValid(ott, request.getRemainMember())) {
            throw new CustomException(ErrorCode.NEWSFEED_REMAIN_MEMBER_OVER);
        }

        // Newsfeed 엔티티 생성 및 저장
        Newsfeed newsfeed = Newsfeed.builder()
                .title(request.getTitle())
                .content(request.getContent())
                .remainMember(request.getRemainMember())
                .user(user)
                .ott(ott)
                .like(0L)
                .build();

        Newsfeed saveNewsfeed = newsfeedRespository.save(newsfeed);

        return new NewsfeedResponseDto(saveNewsfeed);
    }

    @Transactional
    public NewsfeedResponseDto updateNewsFeed(Long newsfeedSeq, NewsfeedRequestDto request, User user) {
        Newsfeed newsfeed = findNewsfeed(newsfeedSeq,user);
        Ott ott = findOtt(request);
        if (!isRemainMembersValid(ott, request.getRemainMember())) {
            throw new CustomException(ErrorCode.NEWSFEED_REMAIN_MEMBER_OVER);
        }
        newsfeed.updateNewsfeed(request,ott);

        return new NewsfeedResponseDto(newsfeed);
    }

    public void deleteNewsFeed(Long newsfeedSeq, User user) {
        Newsfeed newsfeed = findNewsfeed(newsfeedSeq, user);
        newsfeedRespository.delete(newsfeed);
    }

    // 좋아요 많은 순 또는 생성일자 기준 최신순 선택
    private Sort getSortBy(String sortBy) {
        if ("like".equals(sortBy)) {
            return Sort.by("like").descending();
        } else {
            return Sort.by("createdAt").descending();
        }
    }

    // 좋아요 증감 함수
    @Transactional
    public void increaseNewsfeedLike(Long newsfeedSeq){
        Newsfeed newsfeed = findNewsfeed(newsfeedSeq);
        newsfeed.increaseLike();
    }

    @Transactional
    public void decreaseNewsfeedLike(Long newsfeedSeq){
        Newsfeed newsfeed = findNewsfeed(newsfeedSeq);
        newsfeed.decreaseLike();
    }

    //조회는 id 값만 존재하다면 user 상관없이 조회되어야함.
    public Newsfeed findNewsfeed(Long newsfeedSeq) {
        return newsfeedRespository.findById(newsfeedSeq)
                .orElseThrow(() -> new CustomException(ErrorCode.NEWSFEED_NOT_FOUND));

    }

    // 좋아요 유효성 검사
    public void validateNewsfeedLike(Long userId, Long newsfeedSeq) {
        Newsfeed newsfeed = newsfeedRespository.findById(newsfeedSeq).orElseThrow(() ->
                new CustomException(ErrorCode.NEWSFEED_NOT_FOUND));
        if (newsfeed.getUser().getUserSeq().equals(userId)) {
            throw new CustomException(ErrorCode.NEWSFEED_SAME_USER);
        }
    }

    //삭제, 수정에 필요한 newsfeed 가져오는 메서드
    private Newsfeed findNewsfeed(Long newsfeedSeq, User user) {
        if (!newsfeedRespository.existsById(newsfeedSeq)) {
            throw new CustomException(ErrorCode.NEWSFEED_NOT_FOUND);
        }
        return newsfeedRespository.findByNewsFeedSeqAndUser(newsfeedSeq, user)
                .orElseThrow(() -> new CustomException(ErrorCode.NEWSFEED_NOT_USER));
    }

    private Ott findOtt(NewsfeedRequestDto request) {
        return ottRepository.findByOttName(request.getOttName()).
                orElseThrow(()-> new CustomException(ErrorCode.OTT_NOT_FOUND));
    }


    private boolean isRemainMembersValid(Ott ott, int remainMembers) {
        return remainMembers <= ott.getMaxMember();
    }

}
