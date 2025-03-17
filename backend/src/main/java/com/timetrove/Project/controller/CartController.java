package com.timetrove.Project.controller;

import com.timetrove.Project.dto.CartDto;
import com.timetrove.Project.service.CartService;
import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/cart")
public class CartController {

    private final CartService cartService;

    /**
     * @param userCode 사용자 코드
     * @param cartDto 장바구니에 추가할 상품 정보
     */
    @Operation(summary = "장바구니에 상품 추가", description = "사용자의 장바구니에 새로운 상품을 추가합니다.")
    @PostMapping
    public ResponseEntity<String> addToCart(@AuthenticationPrincipal Long userCode, @RequestBody CartDto cartDto) {
        Long createdId = cartService.addToCart(userCode, cartDto);
        return ResponseEntity.ok("장바구니에 등록되었습니다. cart_id : " + createdId);    }

    /**
     * @param userCode 사용자 코드
     * @param cartId 장바구니 항목 ID
     * @param cartDto 변경할 상품 수량 정보
     */
    @Operation(summary = "장바구니 상품 수량 변경", description = "장바구니에 담긴 특정 상품의 수량을 변경합니다.")
    @PutMapping("/{cartId}")
    public ResponseEntity<Void> updateCartItem(@AuthenticationPrincipal Long userCode, @PathVariable("cartId") Long cartId, @RequestBody CartDto cartDto) {
        cartService.updateCartItem(userCode, cartId, cartDto);
        return ResponseEntity.ok().build();
    }

    /**
     * @param userCode 사용자 코드
     * @param cartId 장바구니 항목 ID
     */
    @Operation(summary = "장바구니에서 상품 삭제", description = "장바구니에 담긴 특정 상품을 삭제합니다.")
    @DeleteMapping("/{cartId}")
    public ResponseEntity<Void> removeFromCart(@AuthenticationPrincipal Long userCode, @PathVariable("cartId") Long cartId) {
        cartService.deleteCartItem(userCode, cartId);
        return ResponseEntity.ok().build();
    }

}
