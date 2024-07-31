package com.timetrove.Project.repository;

import java.util.List;

import com.timetrove.Project.domain.Watch;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;


public interface WatchRepository extends JpaRepository<Watch, Long>, WatchRepositoryCustom{
	
	@Query(value = "SELECT * FROM watch "
			     + "ORDER BY hit DESC "
			     + "LIMIT 0,6",nativeQuery = true)
	public List<Watch> getWatchTop6Byhit();
	
	public Watch findByNo(Long no);
	
	@Query(value = "SELECT COUNT(*) FROM watch "
				 +"WHERE name LIKE CONCAT('%',:searchWord,'%') "
				 +"OR model LIKE CONCAT('%',:searchWord,'%') ",
			      nativeQuery = true)
	public int watchFindCount(@Param("searchWord") String searchWord);

}
