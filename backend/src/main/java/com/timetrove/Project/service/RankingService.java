package com.timetrove.Project.service;

import com.timetrove.Project.domain.Board;
import com.timetrove.Project.domain.Product;
import com.timetrove.Project.dto.BoardDto;
import com.timetrove.Project.dto.RankedListDto;
import com.timetrove.Project.dto.ProductDto;
import com.timetrove.Project.repository.BoardRepository;
import com.timetrove.Project.repository.ProductManagementRepository;
import com.timetrove.Project.repository.querydsl.ProductRepository;
import com.timetrove.Project.repository.redis.RedisRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.sql.Timestamp;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class RankingService {
    private static final String PRODUCT_RANKING_KEY = "product_ranking";
    private static final String BOARD_RANKING_KEY = "board_ranking";

    private final RedisRepository redisRepository;
    private final ProductManagementRepository productManagementRepository;
    private final ProductRepository productRepository;
    private final BoardRepository boardRepository;

    // 배치 작업 관련 상수 추가
    private static final long BATCH_SYNC_INTERVAL = 15; // 15분마다 배치 동기화

    // 변경된 데이터를 기록하기 위한 Set
    private final Set<Long> updatedProductIds = ConcurrentHashMap.newKeySet();
    private final Set<Long> updatedBoardIds = ConcurrentHashMap.newKeySet();

    /**
     * 상위 3개의 게시글과 상위 6개의 상품 목록을 반환하는 메서드
     * @return RankedListDto
     */
    public RankedListDto getRanked3BoardsAnd6Products() {
        return RankedListDto.builder()
                .topProductList(getTop6Products())
                .topBoardList(getTop3Boards())
                .build();
    }

    /**
     * 랭킹을 위한 게시글 점수 업데이트 메서드
     * @param: Board board - 점수를 업데이트할 게시글
     */
    public void updateBoardScore(Board board) {
        redisRepository.incrementScore(BOARD_RANKING_KEY, board.getNo().toString(), 1.0);

        // 업데이트된 게시글 ID 기록
        updatedBoardIds.add(board.getNo());
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
     * 랭킹을 위한 상품 점수 업데이트 메서드
     * @param: Long ProductId - 점수를 업데이트할 상품 ID
     */
    public void updateProductScore(Long productId) {
        redisRepository.incrementScore(PRODUCT_RANKING_KEY, productId.toString(), 1.0);

        // 업데이트된 상품 ID 기록
        updatedProductIds.add(productId);
    }


    /**
     * 상위 6개의 상품 ID 조회 및 변환 메서드
     * @return ProductDto 리스트
     */
    public List<ProductDto> getTop6Products() {
        Set<Object> topProductIdsSet = redisRepository.getTopRankedItems(PRODUCT_RANKING_KEY, 6);

        return topProductIdsSet.stream()
                .map(this::safelyParseLong)
                .filter(Optional::isPresent)
                .map(Optional::get)
                .map(productId -> productRepository.findById(productId)
                        .map(ProductDto::convertProductToDto)
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

    /**
     * Redis 데이터를 RDB로 주기적으로 동기화하는 배치 메서드
     * Spring Scheduled 어노테이션을 사용하여 스케줄링
     */
    @Scheduled(fixedRate = BATCH_SYNC_INTERVAL * 60 * 1000) // 분 단위를 밀리초로 변환
    public void syncRedisToRDB() {
        syncProductDataToRDB();
        syncBoardDataToRDB();
    }

    /**
     * 상품 조회수 데이터를 RDB에 동기화하는 메서드
     */
    private void syncProductDataToRDB() {
        // 변경된 상품 ID가 없으면 처리하지 않음
        if (updatedProductIds.isEmpty()) {
            return;
        }

        // Redis에서 현재 상품 점수 가져오기
        for (Long productId : updatedProductIds) {
            Double score = redisRepository.getScore(PRODUCT_RANKING_KEY, productId.toString());
            if (score != null) {
                // 상품 조회수 업데이트
                productManagementRepository.findById(productId).ifPresent(productManagement -> {
                    productManagement.increaseViewCount(score.intValue());
                    productManagementRepository.save(productManagement);
                });
            }
        }

        // 처리 완료된 ID 목록 초기화
        updatedProductIds.clear();
    }

    /**
     * 게시글 점수 데이터를 RDB에 동기화하는 메서드
     */
    private void syncBoardDataToRDB() {
        // 변경된 게시글 ID가 없으면 처리하지 않음
        if (updatedBoardIds.isEmpty()) {
            return;
        }

        // Redis에서 현재 게시글 점수 가져오기
        for (Long boardId : updatedBoardIds) {
            Double score = redisRepository.getScore(BOARD_RANKING_KEY, boardId.toString());
            if (score != null) {
                // 게시글 점수 업데이트
                boardRepository.findById(boardId).ifPresent(board -> {
                    board.increaseScore(score.intValue());
                    boardRepository.save(board);
                });
            }
        }

        // 처리 완료된 ID 목록 초기화
        updatedBoardIds.clear();
    }

}
