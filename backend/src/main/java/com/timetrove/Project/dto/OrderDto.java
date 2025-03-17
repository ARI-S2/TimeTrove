package com.timetrove.Project.dto;

import com.timetrove.Project.common.enumType.PayMethod;
import com.timetrove.Project.domain.Order;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Collectors;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class OrderDto {
    private Long orderId;
    private String merchantUid;
    private String ordererName;
    private String productNames;
    private BigDecimal totalPrice;
    private String postCode;
    private String address;
    private String detailAddress;
    PayMethod payMethod;

    // Entity -> DTO
    public static OrderDto from(Order order) {
        return OrderDto.builder()
                .orderId(order.getOrderId())
                .merchantUid(order.getMerchantUid())
                .productNames(order.getProductNames())
                .totalPrice(order.getTotalPrice())
                .postCode(order.getPostCode())
                .address(order.getAddress())
                .detailAddress(order.getDetailAddress())
                .ordererName(order.getOrdererName())
                .payMethod(order.getPayMethod())
                .build();
    }

    // DTO 리스트 변환
    public static List<OrderDto> from(List<Order> orders) {
        return orders.stream()
                .map(OrderDto::from)
                .collect(Collectors.toList());
    }
}