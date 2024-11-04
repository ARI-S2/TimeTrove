package com.timetrove.Project.service;

import com.timetrove.Project.domain.Watch;
import com.timetrove.Project.dto.PageResponse;
import com.timetrove.Project.dto.WatchDto;
import com.timetrove.Project.repository.querydsl.WatchRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

@Service
@RequiredArgsConstructor
public class WatchService {

	private final WatchRepository watchRepository;
	private final RankingService rankingService;

	private static final int PAGE_SIZE = 6;

	/**
	 * 시계 조회 및 조회수 증가 메서드
	 * @param: Long id - 시계 ID
	 * @return WatchDto
	 */
	@Transactional
	public WatchDto getWatchByIdAndIncreaseVeiwCount(Long id) {
		Watch watch = watchRepository.findByIdWithPessimisticLock(id);
		watch.increaseViewCount();
		watchRepository.save(watch);
		rankingService.updateWatchScore(id);
		return WatchDto.convertWatchToDto(watch);
	}

	/**
	 * 시계 리스트 조회 메서드
	 * 페이지 번호, 검색어, 필터를 기준으로 시계 리스트를 조회
	 * @param: int page - 페이지 번호
	 * @param: String searchWord - 검색어
	 * @param: String filter - 필터 조건
	 * @return 페이징된 WatchDto 리스트
	 */
	@Transactional
	public PageResponse<WatchDto> findWatchList(int page, String searchWord, String filter) {
		Pageable pageable = PageRequest.of(page - 1, PAGE_SIZE);
		Page<Watch> watchPage = watchRepository.findWatchesWithFilter(searchWord, filter, pageable);
		return PageResponse.fromPage(watchPage.map(WatchDto::convertWatchToDto));
	}
}
