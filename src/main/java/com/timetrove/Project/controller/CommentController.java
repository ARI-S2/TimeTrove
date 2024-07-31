package com.timetrove.Project.controller;

import java.util.List;

import com.timetrove.Project.dto.CommentDto;
import com.timetrove.Project.service.CommentService;
import org.springframework.http.ResponseEntity;
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
	   /*	
	   @PostMapping("/{no}/reply/add")
	   public ResponseEntity<List<Reply>> addReply(@PathVariable("no") Integer no, @RequestBody Reply reply){
		   List<Reply> newReplies = null;
		   try
		    {
			   newReplies = commentService.addReply(no, reply);
		    }catch(Exception ex)
		    {
		    	return new ResponseEntity<>(null,HttpStatus.INTERNAL_SERVER_ERROR);
	 	    }
	       return new ResponseEntity<>(newReplies,HttpStatus.OK);
	   }
	   */
	   @PostMapping("/add")
	   public ResponseEntity<CommentDto> createComment(@RequestBody CommentDto commentDto) {
	       CommentDto commentResponseDto = commentService.createComment(commentDto);
	       return ResponseEntity.ok().body(commentResponseDto);
	   }
	   
	   @PutMapping("/edit/{commentId}")
	   public ResponseEntity<CommentDto> updateComment(@PathVariable("commentId") Long commentId , @RequestBody CommentDto commentDto) {
		   CommentDto updatedComment = commentService.updateComment(commentId, commentDto);
	       return ResponseEntity.ok().body(updatedComment);
	   }

	   @DeleteMapping("/delete/{commentId}")
	   public ResponseEntity<Void> deleteComment(@PathVariable("commentId") Long commentId) {
	       commentService.deleteComment(commentId);
	       return ResponseEntity.noContent().build();
	   }
	    
	   @GetMapping("/{no}")
	   public ResponseEntity<List<CommentDto>> getSnsComment(@PathVariable("no") Long no) {
	       List<CommentDto> CommentDtoList = commentService.findCommentListByNo(no);

	       return ResponseEntity.ok().body(CommentDtoList);
	   }
	 
}
