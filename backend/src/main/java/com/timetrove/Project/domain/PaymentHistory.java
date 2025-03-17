package com.timetrove.Project.domain;

import com.timetrove.Project.common.enumType.PaymentStatus;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Entity
@Getter
@Builder
@AllArgsConstructor
@Table(name = "payment_history")
public class PaymentHistory {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "payment_history_id")
    private Long id; // PK

    @ManyToOne
    @JoinColumn(name = "user_code", referencedColumnName = "user_code", nullable = false)
    private User user; // 사용자

    @ManyToOne
    @JoinColumn(name = "order_id", nullable = false)
    private Order order; // 주문 테이블과 다대일 (연관관계 주인은 주문)

    @ManyToOne
    @JoinColumn(name = "product", nullable = false)
    private Product product; // 상품

    @Column(name = "impUid")
    private String impUid;

    @Column(name = "pay_method")
    private String payMethod;

    @Column(name = "product_name")
    private String productName; // 상품 이름

    @Column(name = "product_price", nullable = false)
    private Integer price; // 가격

    @Column(name = "total_price", nullable = false)
    private Long totalPrice; // 결제한 총 가격

    @Column(name = "paid_at", nullable = false, columnDefinition = "TIMESTAMP DEFAULT CURRENT_TIMESTAMP")
    private LocalDateTime paidAt; // 결제시각

    @Column(name = "status")
    @Enumerated(EnumType.STRING)
    private PaymentStatus statusType;

    private String bankCode;
    private String bankName;
    private String buyerAddr;
    private String buyerEmail;

    public PaymentHistory() {
        this.paidAt =  LocalDateTime.now();
    }

    // 첫 번째 썸네일을 가져오는 헬퍼 메서드 추가
    public String getFirstThumbnailImage() {
        if (product != null && !product.getImage().isEmpty()) {
            return product.getImage();
        }
        return null;
    }
}