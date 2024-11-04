package com.timetrove.Project.repository.querydsl;

import com.timetrove.Project.domain.Watch;
import jakarta.persistence.LockModeType;
import jakarta.persistence.QueryHint;
import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.query.Param;


public interface WatchRepository extends JpaRepository<Watch, Long>, WatchRepositoryCustom{

	@Lock(LockModeType.PESSIMISTIC_WRITE) // 비관적 락
	@QueryHints({@QueryHint(name = "jakarta.persistence.lock.timeout", value = "3000")}) // 데드락 발생 시 timeout 설정
	@Query("SELECT w FROM Watch w WHERE w.id = :id")
	Watch findByIdWithPessimisticLock(@Param("id") Long id);


	@Query("SELECT w FROM Watch w WHERE w.id = :id")
	Watch findByWatchId(@Param("id") Long id);

}
