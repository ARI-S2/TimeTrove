package com.timetrove.Project.controller;

import com.timetrove.Project.dto.OrderDto;
import com.timetrove.Project.service.OrderService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/v1/order")
@RequiredArgsConstructor
public class OrderController {
    private final OrderService orderService;

    /**
     * 주문서에 나타낼 정보
     * @param payload  "cartIds" : [1,2,3]
     * @return
     */
    @PostMapping("/create")
    public ResponseEntity<String> createOrder(@AuthenticationPrincipal Long userCode,
                                              @RequestBody Map<String, Object> payload) {
        List<Integer> cartIdsInteger = (List<Integer>) payload.get("cartIds");
        List<Long> cartIds = cartIdsInteger.stream()
                .map(Long::valueOf)
                .collect(Collectors.toList());

        orderService.createTemporaryOrder(userCode, cartIds);
        return ResponseEntity.ok("주문 임시 저장 완료");
    }

    /**
     * 주문서에서 입력받아 최종 주문 테이블 생성
     * @param request
     * @return
     */
    @PostMapping("/done")
    public ResponseEntity<Object> completeOrder(@AuthenticationPrincipal Long userCode,
                                                @RequestBody OrderDto request) {
        OrderDto orderResponseDto = orderService.completeOrder(userCode, request);
        return ResponseEntity.ok(orderResponseDto);
    }

}