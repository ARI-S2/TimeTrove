package com.timetrove.Project.service;

import com.timetrove.Project.domain.Board;
import com.timetrove.Project.domain.Watch;
import com.timetrove.Project.dto.BoardDto;
import com.timetrove.Project.dto.RankedListDto;
import com.timetrove.Project.dto.WatchDto;
import com.timetrove.Project.repository.BoardRepository;
import com.timetrove.Project.repository.querydsl.WatchRepository;
import com.timetrove.Project.repository.redis.RedisRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.sql.Timestamp;
import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class RankingService {
    private static final String WATCH_RANKING_KEY = "watch_ranking";
    private static final String BOARD_RANKING_KEY = "board_ranking";

    private final RedisRepository redisRepository;
    private final WatchRepository watchRepository;
    private final BoardRepository boardRepository;

    /**
     * 상위 3개의 게시글과 상위 6개의 시계 목록을 반환하는 메서드
     * @return RankedListDto
     */
    public RankedListDto getRanked3BoardsAnd6Watches() {
        return RankedListDto.builder()
                .topWatchList(getTop6Watches())
                .topBoardList(getTop3Boards())
                .build();
    }

    /**
     * 랭킹을 위한 게시글 점수 업데이트 메서드
     * @param: Board board - 점수를 업데이트할 게시글
     */
    public void updateBoardScore(Board board) {
        double score = calculateBoardScore(board);
        redisRepository.incrementScore(BOARD_RANKING_KEY, board.getNo().toString(), score);
    }

    /**
     * 게시글 점수 계산 메서드
     * Hacker News 랭킹 알고리즘을 기반으로 함
     * @param: Board board - 점수를 계산할 게시글
     * @return 계산된 점수
     */
    public double calculateBoardScore(Board board) {
        int score = board.getScore();
        Timestamp createdAt = board.getCreatedAt();
        long hoursSinceCreation = TimeUnit.MILLISECONDS.toHours(
                System.currentTimeMillis() - createdAt.getTime()
        );

        return (score - 1) / Math.pow(hoursSinceCreation + 2, 1.8);
    }

    /**
     * 상위 3개의 게시글 ID 조회 및 변환 메서드
     * @return BoardDto 리스트
     */
    public List<BoardDto> getTop3Boards() {
        Set<Object> topBoardIdsSet = redisRepository.getTopRankedItems(BOARD_RANKING_KEY, 3);

        return topBoardIdsSet.stream()
                .map(this::safelyParseLong)
                .filter(Optional::isPresent)
                .map(Optional::get)
                .map(boardId -> boardRepository.findById(boardId)
                        .map(BoardDto::convertBoardToDto)
                        .orElse(null))
                .filter(Objects::nonNull)
                .collect(Collectors.toList());
    }

    /**
     * 랭킹을 위한 시계 점수 업데이트 메서드
     * @param: Long watchId - 점수를 업데이트할 시계 ID
     */
    public void updateWatchScore(Long watchId) {
        double score = calculateWatchScore(watchRepository.findByWatchId(watchId));
        redisRepository.incrementScore(WATCH_RANKING_KEY, watchId.toString(), score);
    }

    /**
     * 시계 점수 계산 메서드
     * @param: Watch watch - 점수를 계산할 시계
     * @return 계산된 점수
     */
    private double calculateWatchScore(Watch watch) {
        double viewWeight = 1;
        double cartWeight = 3;
        double purchaseWeight = 5;

        return (watch.getViewCount() * viewWeight) +
                (watch.getCartCount() * cartWeight) +
                (watch.getPurchaseCount() * purchaseWeight);
    }

    /**
     * 상위 6개의 시계 ID 조회 및 변환 메서드
     * @return WatchDto 리스트
     */
    public List<WatchDto> getTop6Watches() {
        Set<Object> topWatchIdsSet = redisRepository.getTopRankedItems(WATCH_RANKING_KEY, 6);

        return topWatchIdsSet.stream()
                .map(this::safelyParseLong)
                .filter(Optional::isPresent)
                .map(Optional::get)
                .map(watchId -> watchRepository.findById(watchId)
                        .map(WatchDto::convertWatchToDto)
                        .orElse(null))
                .filter(Objects::nonNull)
                .collect(Collectors.toList());
    }

    /**
     * 객체를 Long 타입으로 변환하는 메서드
     * @param: Object obj - 변환할 객체
     * @return 변환된 Long 객체 (Optional)
     */
    private Optional<Long> safelyParseLong(Object obj) {
        try {
            if (obj instanceof Long) {
                return Optional.of((Long) obj);
            } else if (obj instanceof Integer) {
                return Optional.of(((Integer) obj).longValue());
            } else if (obj instanceof String) {
                return Optional.of(Long.parseLong((String) obj));
            }
            return Optional.empty();
        } catch (NumberFormatException e) {
            return Optional.empty();
        }
    }
}
