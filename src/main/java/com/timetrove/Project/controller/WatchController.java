package com.timetrove.Project.controller;

import java.util.HashMap;
import java.util.Map;

import com.timetrove.Project.domain.Watch;
import com.timetrove.Project.service.WatchService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.timetrove.Project.domain.User;

import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
public class WatchController {
	private final WatchService service;
    
    @GetMapping("/")
    public ResponseEntity<Map> main_data(){
    	Map map=new HashMap<>();
    	try
    	{	
    		map = service.getHomeData();
    	}catch(Exception ex)
    	{
    		return new ResponseEntity<>(null,HttpStatus.INTERNAL_SERVER_ERROR);
    	}
    	return new ResponseEntity<>(map,HttpStatus.OK);
    }
    
    @GetMapping("/watch/list")
    public ResponseEntity<Map> watch_list(
 		   @RequestParam("page") int page,
 		   @RequestParam(value = "searchWord", required = false) String searchWord, 
 		   @RequestParam("filter") String filter) {
    	
 	   if (searchWord == null) searchWord = ""; 
 	   Map map = new HashMap();
 	   try
 	   {
 		   System.out.println(filter);
 		   map = service.findWatchList(page, searchWord, filter);
 	   }catch(Exception ex)
 	   {
 		   return new ResponseEntity<>(null,HttpStatus.INTERNAL_SERVER_ERROR);
 	   }
 	   return new ResponseEntity<>(map,HttpStatus.OK);
    }
    
    @GetMapping("/watch/detail/{no}")
    public ResponseEntity<Watch> watchDetailData(@PathVariable("no") Long no)
    {
  	    Watch watch=null;
  	    try
  	    {
  	    	watch = service.getWatchByNO(no);
  	    }catch(Exception ex)
  	    {
  	    	return new ResponseEntity<>(null,HttpStatus.INTERNAL_SERVER_ERROR);
  	    }
  	    return new ResponseEntity<>(watch,HttpStatus.OK);
    }
    
}
