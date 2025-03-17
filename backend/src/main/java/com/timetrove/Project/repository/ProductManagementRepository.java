package com.timetrove.Project.repository;

import com.timetrove.Project.domain.ProductManagement;
import jakarta.persistence.LockModeType;
import jakarta.persistence.QueryHint;
import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ProductManagementRepository extends JpaRepository<ProductManagement, Long> {
    // 상품 ID로 상품 관리 정보 찾기
    Optional<ProductManagement> findByProduct_ProductId(Long productId);

    @Lock(LockModeType.PESSIMISTIC_WRITE) // 비관적 락
    @QueryHints({@QueryHint(name = "jakarta.persistence.lock.timeout", value = "3000")}) // 데드락 발생 시 timeout 설정
    @Query("SELECT pm FROM ProductManagement pm WHERE pm.inventoryId = :id")
    ProductManagement findByIdWithPessimisticLock(@Param("id") Long id);

    /**
     * 동시성 문제를 해결한 상품의 조회수 증가 （데이터베이스 레벨에서 수정 )
     * @Modifying: 수정 쿼리임을 명시하여 영속성 컨텍스트를 무시하고 직접 데이터베이스에 쿼리를 실행
     * @Query: 상품 관리 테이블의 조회수를 증가시키는 업데이트 쿼리 수행
     */
    @Modifying
    @Query("UPDATE ProductManagement pm SET pm.viewCount = pm.viewCount + 1 WHERE pm.product.productId = :productId")
    void incrementViewCount(@Param("productId") Long productId);

    // 재고가 있는 상품 관리 정보 찾기
    List<ProductManagement> findByQuantityGreaterThan(Long quantity);

    // 품절된 상품 관리 정보 찾기
    List<ProductManagement> findByIsSoldOutTrue();

    // 재입고 가능한 상품 관리 정보 찾기
    List<ProductManagement> findByIsRestockAvailableTrue();
}