package com.timetrove.Project.controller;
import java.util.*;

import com.timetrove.Project.domain.Board;
import com.timetrove.Project.service.BoardService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;


import lombok.RequiredArgsConstructor;


@RestController
@RequiredArgsConstructor
public class BoardController {
   private final BoardService service;
   
   @GetMapping("/board/list")
   public ResponseEntity<Map> boardList(
		   @RequestParam("page") int page,
		   @RequestParam(value = "searchWord", required = false) String searchWord) {

	   if (searchWord == null) 
		   searchWord = ""; 
	   Map map = new HashMap();
	   try
	   {
		   map = service.findBoardList(page,searchWord);
	   }catch(Exception ex)
	   {
		   return new ResponseEntity<>(null,HttpStatus.INTERNAL_SERVER_ERROR);
	   }
	   return new ResponseEntity<>(map,HttpStatus.OK);
   }
   
   @GetMapping("/board/detail/{no}")
   public ResponseEntity<Board> boardDetailData(@PathVariable("no") Long no){
	    Board board = null;
 	    try
 	    {
 	    	board = service.getBoardByNO(no);
 	    }catch(Exception ex)
 	    {
 	    	return new ResponseEntity<>(null,HttpStatus.INTERNAL_SERVER_ERROR);
 	    }
 	    return new ResponseEntity<>(board,HttpStatus.OK);
   }
   

}