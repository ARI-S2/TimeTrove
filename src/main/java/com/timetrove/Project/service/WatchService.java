package com.timetrove.Project.service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.timetrove.Project.domain.Board;
import com.timetrove.Project.domain.Watch;
import com.timetrove.Project.repository.BoardRepository;
import com.timetrove.Project.repository.WatchRepository;
import org.springframework.stereotype.Service;



import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class WatchService {
    private final WatchRepository watchRepository;
    private final BoardRepository boardRepository;
    
    public Map getHomeData() {
        Map map = new HashMap<>();
        List<Watch> wList = watchRepository.getWatchTop6Byhit();
        List<Board> bList = boardRepository.getBoardLatest3();
        map.put("wList", wList);
        map.put("bList", bList);
        return map;
    }
    
    public Map findWatchList(int page, String searchWord, String filter) {
		Map map = new HashMap();
	    int rowSize = 6;
	    int start = ( rowSize * page ) - rowSize;
	    List<Watch> wList = watchRepository.watchFindData(searchWord,start,filter);
	    int count = searchWord != "" ? watchRepository.watchFindCount(searchWord) : (int)watchRepository.count();
	    map.put("wList", wList);
	    map.put("count", count);
	    map.put("curpage", page);
	    return map;
	}
	
	public Watch getWatchByNO(Long no) { 
		return watchRepository.findByNo(no);
	}
}
