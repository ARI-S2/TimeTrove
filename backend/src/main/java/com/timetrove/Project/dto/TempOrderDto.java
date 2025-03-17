package com.timetrove.Project.dto;

import com.timetrove.Project.domain.Order;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.List;

/**
 * Redis에 임시 주문 정보와 장바구니 ID를 함께 저장하기 위한 DTO
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TempOrderDto implements Serializable {
    private Order order;
    private List<Long> cartIds;
}
