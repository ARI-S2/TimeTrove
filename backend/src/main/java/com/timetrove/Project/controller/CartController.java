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
    public ResponseEntity<Void> addToCart(@AuthenticationPrincipal Long userCode, @RequestBody CartDto cartDto) {
        cartService.addWatchToCart(userCode, cartDto);
        return ResponseEntity.ok().build();
    }

    /**
     * @param userCode 사용자 코드
     * @param cartId 장바구니 항목 ID
     * @param cartDto 변경할 상품 수량 정보
     */
    @Operation(summary = "장바구니 상품 수량 변경", description = "장바구니에 담긴 특정 상품의 수량을 변경합니다.")
    @PutMapping("/{cartId}")
    public ResponseEntity<Void> updateCartItem(@AuthenticationPrincipal Long userCode, @PathVariable("cartId") Long cartId, @RequestBody CartDto cartDto) {
        cartService.updateWatchInCart(userCode, cartId, cartDto);
        return ResponseEntity.ok().build();
    }

    /**
     * @param userCode 사용자 코드
     * @param cartId 장바구니 항목 ID
     */
    @Operation(summary = "장바구니에서 상품 삭제", description = "장바구니에 담긴 특정 상품을 삭제합니다.")
    @DeleteMapping("/{cartId}")
    public ResponseEntity<Void> removeFromCart(@AuthenticationPrincipal Long userCode, @PathVariable("cartId") Long cartId) {
        cartService.deleteWatchFromCart(userCode, cartId);
        return ResponseEntity.ok().build();
    }

    /**
     * @param userCode 사용자 코드
     * @param cartDtoList 구매할 상품 목록
     */
    @Operation(summary = "여러 상품 동시 구매", description = "장바구니에 담긴 여러 상품을 동시에 구매합니다.")
    @PostMapping("/purchases")
    public ResponseEntity<Void> purchaseItems(@AuthenticationPrincipal Long userCode, @RequestBody List<CartDto> cartDtoList) {
        cartService.processPurchase(cartDtoList);
        return ResponseEntity.ok().build();
    }

    /**
     * @param userCode 사용자 코드
     * @return 구매 내역 (날짜별)
     */
    @Operation(summary = "최근 구매 내역 조회", description = "사용자가 최근에 구매한 상품 목록을 조회합니다.")
    @GetMapping("/purchase_history")
    public ResponseEntity<Map<LocalDateTime, List<CartDto>>> getPurchaseHistory(@AuthenticationPrincipal Long userCode) {
        Map<LocalDateTime, List<CartDto>> purchaseHistory = cartService.getPurchaseHistory(userCode);
        return ResponseEntity.ok(purchaseHistory);
    }
}
