package com.timetrove.Project.controller;

import com.timetrove.Project.dto.BoardDto;
import com.timetrove.Project.dto.PageResponse;
import com.timetrove.Project.service.BoardService;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;


import lombok.RequiredArgsConstructor;


@RestController
@RequestMapping("/api/boards")
@RequiredArgsConstructor
public class BoardController {

   private final BoardService boardService;

    /**
     * @param page 페이지 번호
     * @param searchWord 검색어 (옵션)
     * @return 페이지별 게시글 목록
     */
   @Operation(summary = "게시글 목록 조회", description = "검색어와 페이지 번호를 사용해 게시판 목록을 조회합니다.")
   @GetMapping
   public ResponseEntity<PageResponse<BoardDto>> boardList(
		   @RequestParam("page") int page,
		   @RequestParam(value = "searchWord", required = false) String searchWord) {

       PageResponse<BoardDto> pageResponse = boardService.findBoardList(page, searchWord);
	   return ResponseEntity.ok().body(pageResponse);
   }

    /**
     * @param no 게시글 번호
     * @param request HttpServletRequest 객체
     * @param response HttpServletResponse 객체
     * @return 게시글 상세 정보
     */
    @Operation(summary = "게시글 상세 조회", description = "특정 번호의 게시글을 조회하고 조회수를 증가합니다.")
    @GetMapping("/{no}")
    public ResponseEntity<BoardDto> boardDetailData(
            @PathVariable("no") Long no,
            HttpServletRequest request,
            HttpServletResponse response) {
        return ResponseEntity.ok().body(boardService.getBoardByNo(no, request, response));
    }
}