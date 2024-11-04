package com.timetrove.Project.repository;


import com.timetrove.Project.domain.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<User, Long> {

    User findByUserCode(Long userCode);

}
