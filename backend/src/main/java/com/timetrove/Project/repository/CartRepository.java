package com.timetrove.Project.repository;

import com.timetrove.Project.domain.Cart;
import com.timetrove.Project.domain.ProductManagement;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface CartRepository extends JpaRepository<Cart, Long> {

    List<Cart> findByUser_UserCodeOrderByCartIdDesc(Long userCode);

    Optional<Cart> findByUser_UserCodeAndProductManagement(Long userCode, ProductManagement productManagement);

}
