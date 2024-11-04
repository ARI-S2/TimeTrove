package com.timetrove.Project.service;

import com.timetrove.Project.common.enumType.ErrorCode;
import com.timetrove.Project.common.exception.EntityNotFoundException;
import com.timetrove.Project.domain.Board;
import com.timetrove.Project.dto.BoardDto;
import com.timetrove.Project.dto.PageResponse;
import com.timetrove.Project.repository.BoardRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;


@Service
@RequiredArgsConstructor
public class BoardService {
	private final BoardRepository boardRepository;
	private final RankingService rankingService;

	private static final int PAGE_SIZE = 6;

	/**
	 * 게시글 상세보기 메서드
	 * 게시글 번호로 게시글을 조회하고 조회 수를 증가시킴
	 * @param: Long no - 게시글 번호
	 * @return 게시글 DTO
	 */
	public BoardDto getBoardByNo(Long no) {
		Board board = boardRepository.findById(no)
				.orElseThrow(() -> new EntityNotFoundException(HttpStatus.NOT_FOUND, ErrorCode.BOARD_NOT_FOUND));

		board.increaseScore();
		boardRepository.save(board);
		rankingService.updateBoardScore(board);

		return BoardDto.convertBoardToDto(board);
	}

	/**
	 * 게시글 리스트 조회 메서드
	 * 페이지 번호와 검색어를 기반으로 게시글 리스트 조회
	 * @param: int page - 페이지 번호
	 * @param: String searchWord - 검색어
	 * @return 페이징된 BoardDto 리스트
	 */
	public PageResponse<BoardDto> findBoardList(int page, String searchWord) {
		Pageable pageable = PageRequest.of(page - 1, PAGE_SIZE);
		Page<Board> boardPage = searchWord.isEmpty()
				? boardRepository.findAll(pageable)
				: boardRepository.findBySubjectContainingOrContentContaining(searchWord, searchWord, pageable);

		return PageResponse.fromPage(boardPage.map(BoardDto::convertBoardToDto));
	}
}