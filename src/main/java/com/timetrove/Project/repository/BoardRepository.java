package com.timetrove.Project.repository;

import java.util.List;

import com.timetrove.Project.domain.Board;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;


public interface BoardRepository extends JpaRepository<Board, Long>{
	@Query(value = "SELECT * FROM board "
				 +"WHERE subject LIKE CONCAT('%',:searchWord,'%') "
				 +"OR content LIKE CONCAT('%',:searchWord,'%') "
				 +"ORDER BY no DESC LIMIT :start,6"
				 ,nativeQuery = true)
	public List<Board> boardFindData(@Param("searchWord") String searchWord,@Param("start") int start);
	
	public Board findByNo(Long no);
	
	@Query(value = "SELECT COUNT(*) FROM board "
				 +"WHERE subject LIKE CONCAT('%',:searchWord,'%') "
				 +"OR content LIKE CONCAT('%',:searchWord,'%') ",
			      nativeQuery = true)
	public int boardFindCount(@Param("searchWord") String searchWord);
	
	@Query(value = "SELECT * FROM board "
				+"ORDER BY regdate DESC LIMIT 3"
				,nativeQuery = true)
	public List<Board> getBoardLatest3();
}
