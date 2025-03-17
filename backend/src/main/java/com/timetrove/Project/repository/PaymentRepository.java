package com.timetrove.Project.repository;

import com.timetrove.Project.common.enumType.PaymentStatus;
import com.timetrove.Project.domain.PaymentHistory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;

public interface PaymentRepository extends JpaRepository<PaymentHistory, Long> {
    List<PaymentHistory> findByUser_UserCodeAndPaidAtBetweenOrderByPaidAtDesc(
            @Param("userCode") Long userCode,
            @Param("start") LocalDateTime start,
            @Param("end") LocalDateTime end);

    List<PaymentHistory> findByUser_UserCodeOrderByPaidAtDesc(@Param("userCode") Long userCode);

    // 결제 상태 기준 필터링
    List<PaymentHistory> findByUser_UserCodeAndStatusTypeAndPaidAtBetween(
            @Param("userCode") Long userCode,
            @Param("statusType") PaymentStatus statusType,
            @Param("start") LocalDateTime start,
            @Param("end") LocalDateTime end);
}