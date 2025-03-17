package com.timetrove.Project.dto;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
public class PaymentRequestDto {
    private Long userCode;
    private Long orderId;
    private Long price;
    private List<Long> inventoryIdList;

    public PaymentRequestDto(Long userCode, Long orderId, Long price, List<Long> inventoryIdList) {
        this.userCode = userCode;
        this.orderId = orderId;
        this.price = price;
        this.inventoryIdList = inventoryIdList;
    }
}