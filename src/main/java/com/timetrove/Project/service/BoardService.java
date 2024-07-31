package com.timetrove.Project.service;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.timetrove.Project.domain.Board;
import com.timetrove.Project.repository.BoardRepository;
import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class BoardService {
	private final BoardRepository boardRepository;
	
	public Map<String, Object> findBoardList(int page, String searchWord) {
		Map<String, Object> map = new HashMap();
	    int rowSize = 6;
	    int start = ( rowSize * page ) - rowSize;
	    List<Board> bList = boardRepository.boardFindData(searchWord,start);
	    int count = searchWord != "" ? boardRepository.boardFindCount(searchWord) : (int)boardRepository.count();
	    map.put("bList", bList);
	    map.put("count", count);
	    map.put("curpage", page);
	    return map;
	}
	
	public Board getBoardByNO(Long no) { 
		Board board = boardRepository.findByNo(no);
		return board;
	}

}