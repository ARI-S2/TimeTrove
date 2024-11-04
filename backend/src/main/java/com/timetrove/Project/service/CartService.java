package com.timetrove.Project.service;

import com.timetrove.Project.common.enumType.ErrorCode;
import com.timetrove.Project.common.exception.EntityNotFoundException;
import com.timetrove.Project.domain.Cart;
import com.timetrove.Project.domain.Watch;
import com.timetrove.Project.dto.CartDto;
import com.timetrove.Project.repository.CartRepository;
import com.timetrove.Project.repository.UserRepository;
import com.timetrove.Project.repository.querydsl.WatchRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CartService {

    private final CartRepository cartRepository;
    private final UserRepository userRepository;
    private final WatchRepository watchRepository;
    private final RankingService rankingService;

    /**
     * 장바구니에 시계 추가 메서드
     * 사용자 코드와 장바구니 DTO를 사용하여 시계를 장바구니에 추가
     * @param: Long userCode - 사용자 코드
     * @param: CartDto cartDto - 장바구니 DTO
     */
    @Transactional
    public void addWatchToCart(Long userCode, CartDto cartDto) {
        Watch watch = findWatchByIdOrThrow(cartDto.getWatchId());
        Optional<Cart> existingCartItem = cartRepository.findByUser_UserCodeAndWatchAndPurchasedFalse(userCode, watch);

        if (existingCartItem.isPresent()) {
            // 기존 항목이 존재하면 수량 업데이트
            Cart cart = existingCartItem.get();
            cart.setQuantity(cart.getQuantity() + cartDto.getQuantity());
            cartRepository.save(cart);
        } else {
            // 기존 항목이 없으면 새로운 항목 추가
            cartRepository.save(new Cart(userRepository.findByUserCode(userCode), watch, cartDto.getQuantity(), false, null));
        }

        increaseCartCount(watch);
    }

    /**
     * 장바구니 항목 업데이트 메서드
     * @param: Long userCode - 사용자 코드
     * @param: Long cartId - 장바구니 ID
     * @param: CartDto cartDto - 장바구니 DTO
     */
    public void updateWatchInCart(Long userCode, Long cartId, CartDto cartDto) {
        Cart cart = findCartByIdOrThrow(cartId);
        if (!cart.getUser().getUserCode().equals(userCode)) {
            throw new EntityNotFoundException(HttpStatus.BAD_REQUEST, ErrorCode.INVALID_USER_INFO);
        }

        cart.setWatch(findWatchByIdOrThrow(cartDto.getWatchId()));
        cart.setQuantity(cartDto.getQuantity());
        cartRepository.save(cart);
    }

    /**
     * 장바구니에서 시계 삭제 메서드
     * @param: Long userCode - 사용자 코드
     * @param: Long cartId - 장바구니 ID
     */
    public void deleteWatchFromCart(Long userCode, Long cartId) {
        Cart cart = findCartByIdOrThrow(cartId);
        if (!cart.getUser().getUserCode().equals(userCode)) {
            throw new EntityNotFoundException(HttpStatus.BAD_REQUEST, ErrorCode.INVALID_USER_INFO);
        }
        cartRepository.delete(cart);
    }

    /**
     * 구매 처리 메서드
     * 장바구니 항목 리스트를 구매 처리하고 재고 차감 및 인기상품을 위한 구매 점수 업데이트
     * @param: List<CartDto> cartDtoList - 구매할 장바구니 DTO 리스트
     */
    @Transactional
    public void processPurchase(List<CartDto> cartDtoList) {
        cartDtoList.forEach(cartDto -> {
            decreaseWatchQuantity(cartDto.getWatchId(), cartDto.getQuantity());
            processCartUpdate(cartDto);
            rankingService.updateWatchScore(cartDto.getWatchId());
        });
    }

    /**
     * 구매 내역 조회 메서드
     * @param: Long userCode - 사용자 코드
     * @return 구매 내역 맵 (구매 날짜별로 그룹화된 CartDto 리스트)
     */
    public Map<LocalDateTime, List<CartDto>> getPurchaseHistory(Long userCode) {
        List<Cart> purchaseList = cartRepository.findByUser_UserCodeAndPurchasedOrderByPurchaseDateDesc(userCode, true);
        List<CartDto> cartDtos = CartDto.convertCartListToDto(purchaseList);
        return cartDtos.stream().collect(Collectors.groupingBy(CartDto::getPurchaseDate));
    }

    /**
     * 장바구니 업데이트 메서드
     * 구매 완료 시 장바구니 항목 업데이트
     * @param: CartDto cartDto - 장바구니 DTO
     */
    private void processCartUpdate(CartDto cartDto) {
        Cart cart = findCartByIdOrThrow(cartDto.getId());
        cart.setPurchased(true);
        cart.setPurchaseDate(LocalDateTime.now());
        cartRepository.save(cart);
    }

    /**
     * 시계 재고 감소 메서드
     * @param: Long id - 시계 ID
     * @param: Long quantity - 감소할 수량
     */
    private void decreaseWatchQuantity(Long id, Long quantity) {
        Watch watch = watchRepository.findByIdWithPessimisticLock(id);
        watch.decreaseQuantity(quantity);
        watch.increasePurchaseCount();
        watchRepository.save(watch);
    }

    /**
     * 인기상품 랭킹을 위한 장바구니 점수 증가 메서드
     * @param: Watch watch - 점수를 증가시킬 시계 객체
     */
    private void increaseCartCount(Watch watch) {
        watch.increaseCartCount();
        watchRepository.save(watch);
        rankingService.updateWatchScore(watch.getId());
    }

    /**
     * 시계 조회 메서드
     * 시계가 없을 시 예외 발생 (WATCH_NOT_FOUND)
     * @param: Long id - 시계 ID
     * @return Watch
     */
    private Watch findWatchByIdOrThrow(Long id) {
        return watchRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException(HttpStatus.NOT_FOUND, ErrorCode.WATCH_NOT_FOUND));
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