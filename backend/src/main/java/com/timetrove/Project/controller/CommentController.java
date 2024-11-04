package com.timetrove.Project.controller;

import java.util.List;

import com.timetrove.Project.dto.CommentDto;
import com.timetrove.Project.service.CommentService;
import io.swagger.v3.oas.annotations.Operation;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;



import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/comment")
public class CommentController {

	private final CommentService commentService;

	/**
	 * @param commentDto 작성할 댓글 정보
	 * @return 작성된 댓글 정보
	 */
	@Operation(summary = "댓글 작성", description = "새로운 댓글을 작성합니다.")
	@PostMapping("/add")
	public ResponseEntity<CommentDto> createComment(@RequestBody CommentDto commentDto) {
	   CommentDto commentResponseDto = commentService.createComment(commentDto);
	   return ResponseEntity.ok().body(commentResponseDto);
	}

	/**
	 * @param commentId 수정할 댓글 ID
	 * @param commentDto 수정할 댓글 내용
	 * @return 수정된 댓글 정보
	 */
	@Operation(summary = "댓글 수정", description = "특정 ID의 댓글 내용을 수정합니다.")
	@PutMapping("/edit/{commentId}")
	public ResponseEntity<CommentDto> updateComment(@PathVariable("commentId") Long commentId , @RequestBody CommentDto commentDto) {
	   CommentDto updatedComment = commentService.updateComment(commentId, commentDto);
	   return ResponseEntity.ok().body(updatedComment);
	}

	/**
	 * @param commentId 삭제할 댓글 ID
	 */
	@Operation(summary = "댓글 삭제", description = "특정 ID의 댓글을 삭제합니다.")
	@DeleteMapping("/delete/{commentId}")
	public ResponseEntity<Void> deleteComment(@PathVariable("commentId") Long commentId) {
	   commentService.deleteComment(commentId);
	   return ResponseEntity.ok().build();
	}

	/**
	 * @param no 게시글 번호
	 * @return 해당 게시글의 댓글 목록
	 */
	@Operation(summary = "게시글에 대한 댓글 조회", description = "특정 게시글에 대한 댓글 목록을 조회합니다.")
	@GetMapping("/{no}")
	public ResponseEntity<List<CommentDto>> getComment(@PathVariable("no") Long no) {
	   List<CommentDto> CommentDtoList = commentService.findCommentListByNo(no);

	   return ResponseEntity.ok().body(CommentDtoList);
	}

	/**
	 * @param userCode 사용자 코드
	 * @return 최근 댓글 목록
	 */
	@Operation(summary = "최근 작성한 댓글 조회", description = "사용자가 최근에 작성한 댓글 목록을 조회합니다.")
	@GetMapping("/comment_history")
	public ResponseEntity<List<CommentDto>> getRecentComments(@AuthenticationPrincipal Long userCode) {
		List<CommentDto> recentComments = commentService.getRecentComments(userCode);
		return ResponseEntity.ok().body(recentComments);
	}
}
