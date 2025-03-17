package com.timetrove.Project.service;

import com.timetrove.Project.common.enumType.ErrorCode;
import com.timetrove.Project.common.exception.EntityNotFoundException;
import com.timetrove.Project.domain.Board;
import com.timetrove.Project.dto.BoardDto;
import com.timetrove.Project.dto.PageResponse;
import com.timetrove.Project.repository.BoardRepository;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
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
	public BoardDto getBoardByNo(Long no, HttpServletRequest request, HttpServletResponse response) {
		Board board = boardRepository.findById(no)
				.orElseThrow(() -> new EntityNotFoundException(HttpStatus.NOT_FOUND, ErrorCode.BOARD_NOT_FOUND));

		// 쿠키를 확인하여 조회수 중복 방지 처리
		if (!isBoardAlreadyViewed(no, request)) {
			rankingService.updateBoardScore(board);
			// 새로운 쿠키 생성
			addBoardViewCookie(no, response);
		}

		return BoardDto.convertBoardToDto(board);
	}

	private boolean isBoardAlreadyViewed(Long no, HttpServletRequest request) {
		Cookie[] cookies = request.getCookies();
		if (cookies != null) {
			for (Cookie cookie : cookies) {
				if (cookie.getName().equals("boardView") && cookie.getValue().contains("[" + no + "]")) {
					return true;
				}
			}
		}
		return false;
	}

	private void addBoardViewCookie(Long no, HttpServletResponse response) {
		Cookie cookie = new Cookie("boardView", "[" + no + "]");
		cookie.setPath("/");
		cookie.setHttpOnly(true);
		cookie.setMaxAge(24 * 60 * 60); // 1일 동안 유지
		response.addCookie(cookie);
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