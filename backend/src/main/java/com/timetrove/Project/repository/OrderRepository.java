package com.timetrove.Project.repository;

import com.timetrove.Project.domain.Order;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface OrderRepository extends JpaRepository<Order, Long> {

    List<Order> findByUserUserCodeOrderByOrderDayDesc(Long userCode);

    @Query("SELECT o FROM Order o LEFT JOIN FETCH o.orderItems WHERE o.orderId = :orderId")
    Optional<Order> findByIdWithOrderItems(@Param("orderId") Long orderId);

    @Query("SELECT o FROM Order o " +
            "LEFT JOIN FETCH o.orderItems " +
            "LEFT JOIN FETCH o.paymentHistories " +
            "WHERE o.user.userCode = :userCode")
    List<Order> findByUserUserCodeWithDetails(@Param("userCode") Long userCode);
}
