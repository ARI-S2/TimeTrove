package com.timetrove.Project.repository;


import com.timetrove.Project.domain.Board;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;


public interface BoardRepository extends JpaRepository<Board, Long>{

	Page<Board> findBySubjectContainingOrContentContaining(
			String subject, String content, Pageable pageable);

}
