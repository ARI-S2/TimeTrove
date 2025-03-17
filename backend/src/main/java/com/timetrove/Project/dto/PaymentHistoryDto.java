package com.timetrove.Project.dto;

import com.timetrove.Project.common.enumType.PaymentStatus;
import com.timetrove.Project.domain.Order;
import com.timetrove.Project.domain.PaymentHistory;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class PaymentHistoryDto {
    // 고유 id
    private Long paymentId;
    private Long userCode;
    private Long orderId;
    // Product 관련 field
    private Long productId;
    private String productName;
    private String image;
    private Integer productPrice;
    private Long productQuantity;
    // Order 관련 field
    private String merchantUid; // 주문번호
    private String impUid; // 결제 고유 ID
    private String ordererName;
    private String phoneNumber;
    private String buyerAddr;
    private String buyerEmail;
    private LocalDateTime orderedAt;
    private Long totalPrice;
    private String payMethod;
    private String bankName;
    private LocalDateTime paidAt;
    private PaymentStatus statusType;

    public static PaymentHistoryDto fromEntity(PaymentHistory paymentHistory) {
        PaymentHistoryDto dto = new PaymentHistoryDto();

        // 기본 정보
        dto.setPaymentId(paymentHistory.getId());
        dto.setUserCode(paymentHistory.getUser().getUserCode());
        dto.setOrderId(paymentHistory.getOrder().getOrderId());

        // 상품 관련 정보
        dto.setProductId(paymentHistory.getProduct().getProductId());
        dto.setProductName(paymentHistory.getProductName());
        dto.setImage(paymentHistory.getFirstThumbnailImage());
        dto.setProductPrice(paymentHistory.getPrice());

        // 주문 관련 정보
        Order orders = paymentHistory.getOrder();
        dto.setMerchantUid(orders.getMerchantUid());
        dto.setImpUid(paymentHistory.getImpUid());
        dto.setOrdererName(orders.getOrdererName());
        dto.setPhoneNumber(orders.getPhoneNumber());
        dto.setBuyerAddr(paymentHistory.getBuyerAddr());
        dto.setBuyerEmail(paymentHistory.getBuyerEmail());
        dto.setOrderedAt(orders.getOrderDay());
        dto.setTotalPrice(paymentHistory.getTotalPrice());
        dto.setPayMethod(paymentHistory.getPayMethod());
        dto.setBankName(paymentHistory.getBankName());
        dto.setPaidAt(paymentHistory.getPaidAt());
        dto.setStatusType(paymentHistory.getStatusType());

        return dto;
    }

    // 리스트 변환 유틸리티 메서드
    public static List<PaymentHistoryDto> fromEntityList(List<PaymentHistory> paymentHistories) {
        return paymentHistories.stream()
                .map(PaymentHistoryDto::fromEntity)
                .collect(Collectors.toList());
    }
}