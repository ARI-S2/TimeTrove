package com.timetrove.Project.repository;

import com.timetrove.Project.domain.Cart;
import org.springframework.data.jpa.repository.JpaRepository;

import com.timetrove.Project.domain.Watch;
import java.util.List;
import java.util.Optional;

public interface CartRepository extends JpaRepository<Cart, Long> {

    List<Cart> findByUser_UserCodeAndPurchasedOrderByPurchaseDateDesc(Long userCode, boolean purchased);

    Optional<Cart> findByUser_UserCodeAndWatchAndPurchasedFalse(Long userCode, Watch watch);

}
