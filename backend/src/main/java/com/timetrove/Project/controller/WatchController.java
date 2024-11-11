package com.timetrove.Project.controller;

import com.timetrove.Project.dto.PageResponse;
import com.timetrove.Project.dto.WatchDto;
import com.timetrove.Project.service.WatchService;
import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;


@RestController
@RequestMapping("/api/watches")
@RequiredArgsConstructor
public class WatchController {

	private final WatchService watchService;

	/**
	 * @param page 요청 페이지 번호
	 * @param searchWord 검색어 (옵션)
	 * @param filter 필터 조건 (옵션)
	 * @return 페이지별 시계 목록
	 */
	@Operation(summary = "시계 목록 조회", description = "페이지 번호, 검색어, 필터 조건에 따라 시계 목록을 조회하여 반환합니다.")
	@GetMapping
	public ResponseEntity<PageResponse<WatchDto>> getWatchList(
			@RequestParam(defaultValue = "1") int page,
			@RequestParam(required = false) String searchWord,
			@RequestParam(required = false) String filter) {

		PageResponse<WatchDto> pageResponse = watchService.findWatchList(page, searchWord, filter);
		return ResponseEntity.ok().body(pageResponse);
	}

	/**
	 * @param id 시계 ID
	 * @return 조회된 시계 상세 정보
	 */
	@Operation(summary = "시계 상세정보 조회", description = "ID에 해당하는 시계의 상세정보를 조회하고 조회수를 증가합니다.")
	@GetMapping("/{id}")
	public ResponseEntity<WatchDto> getWatchDetail(@PathVariable("id") Long id) {
		return ResponseEntity.ok(watchService.getWatchByIdAndIncreaseVeiwCount(id));
	}
}
