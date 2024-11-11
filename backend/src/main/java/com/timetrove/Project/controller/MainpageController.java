package com.timetrove.Project.controller;

import com.timetrove.Project.dto.RankedListDto;
import com.timetrove.Project.service.RankingService;
import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;


@RestController
@RequiredArgsConstructor
@Slf4j
public class MainpageController {

    private final RankingService rankingService;

    /**
     * @return 메인 페이지에 표시할 인기 게시글과 시계 목록
     */
    @Operation(summary = "메인 페이지 데이터 조회", description = "상위 3개의 게시글과 6개의 시계 목록을 조회하여 반환합니다.")
    @GetMapping("/api/")
    public ResponseEntity<RankedListDto> main_data(){
        RankedListDto rankingList = rankingService.getRanked3BoardsAnd6Watches();
        return ResponseEntity.ok().body(rankingList);
    }
}
