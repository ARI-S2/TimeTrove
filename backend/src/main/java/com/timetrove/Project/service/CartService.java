package com.timetrove.Project.service;

import com.timetrove.Project.common.enumType.ErrorCode;
import com.timetrove.Project.common.exception.EntityNotFoundException;
import com.timetrove.Project.domain.Cart;
import com.timetrove.Project.domain.ProductManagement;
import com.timetrove.Project.dto.CartDto;
import com.timetrove.Project.repository.CartRepository;
import com.timetrove.Project.repository.ProductManagementRepository;
import com.timetrove.Project.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class CartService {

    private final CartRepository cartRepository;
    private final UserRepository userRepository;
    private final ProductManagementRepository productManagementRepository;

    /**
     * 장바구니에 상품 추가 메서드
     * @param userCode - 사용자 코드
     * @param cartDto - 장바구니 DTO
     * @return Long - 장바구니 ID
     */
    @Transactional
    public Long addToCart(Long userCode, CartDto cartDto) {
        // 상품 관리 정보 조회
        ProductManagement productMgt = productManagementRepository.findById(cartDto.getInventoryId())
                .orElseThrow(() -> new EntityNotFoundException(HttpStatus.NOT_FOUND,ErrorCode.PRODUCT_NOT_FOUND));

        // 회원, 상품 정보로 장바구니 객체 조회
        Optional<Cart> existingCart = cartRepository.findByUser_UserCodeAndProductManagement(userCode, productMgt);

        if (existingCart.isPresent()) {
            // 이미 담은 상품인 경우 수량, 가격 수정
            Cart cart = existingCart.get();
            Long newQuantity = cart.getQuantity() + cartDto.getQuantity();

            // 수량과 가격 업데이트
            cart.setQuantity(newQuantity);
            cart.setPrice(cart.getPrice() + productMgt.getProduct().getPrice() * cartDto.getQuantity());
            cartRepository.save(cart);
            return cart.getCartId();
        } else {
            // 새로운 상품인 경우 새로 저장
            Cart newCart = new Cart(
                    userRepository.findByUserCode(userCode),
                    productMgt,
                    cartDto.getQuantity(),
                    productMgt.getProduct().getPrice() * cartDto.getQuantity()
            );
            cartRepository.save(newCart);
            return newCart.getCartId();
        }
    }

    /**
     * 장바구니 항목 수정 메서드
     * @param userCode - 사용자 코드
     * @param cartId - 장바구니 ID
     * @param cartDto - 장바구니 DTO
     */
    @Transactional
    public void updateCartItem(Long userCode, Long cartId, CartDto cartDto) {
        Cart cart = findCartByIdOrThrow(cartId);
        if (!cart.getUser().getUserCode().equals(userCode)) {
            throw new EntityNotFoundException(HttpStatus.BAD_REQUEST, ErrorCode.INVALID_USER_INFO);
        }

        ProductManagement productMgt = cart.getProductManagement();

        // 수량과 가격 업데이트
        cart.setQuantity(cartDto.getQuantity());
        cart.setPrice(productMgt.getProduct().getPrice() * cartDto.getQuantity());
        cartRepository.save(cart);
    }

    /**
     * 장바구니 항목 삭제 메서드
     * @param userCode - 사용자 코드
     * @param cartId - 장바구니 ID
     */
    @Transactional
    public void deleteCartItem(Long userCode, Long cartId) {
        Cart cart = findCartByIdOrThrow(cartId);
        if (!cart.getUser().getUserCode().equals(userCode)) {
            throw new EntityNotFoundException(HttpStatus.BAD_REQUEST, ErrorCode.INVALID_USER_INFO);
        }
        cartRepository.delete(cart);
    }

    /**
     * 장바구니 조회 메서드
     * 장바구니가 없을 시 예외 발생 (CART_NOT_FOUND)
     * @param: Long id - 장바구니 ID
     * @return Cart
     */
    private Cart findCartByIdOrThrow(Long id) {
        return cartRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException(HttpStatus.NOT_FOUND, ErrorCode.CART_NOT_FOUND));
    }
}