package com.timetrove.Project.service;

import com.timetrove.Project.common.config.redis.RedisProperties;
import com.timetrove.Project.common.enumType.ErrorCode;
import com.timetrove.Project.common.exception.EntityNotFoundException;
import com.timetrove.Project.domain.*;
import com.timetrove.Project.dto.CartDto;
import com.timetrove.Project.dto.OrderDto;
import com.timetrove.Project.dto.TempOrderDto;
import com.timetrove.Project.repository.CartRepository;
import com.timetrove.Project.repository.OrderRepository;
import com.timetrove.Project.repository.UserRepository;
import com.timetrove.Project.repository.redis.RedisRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(rollbackFor = Exception.class)
public class OrderService {
    private final CartRepository cartRepository;
    private final OrderRepository orderRepository;
    private final UserRepository userRepository;
    private final RedisRepository redisRepository;

    /**
     * 임시 주문 생성 및 Redis 저장
     */
    public void createTemporaryOrder(Long userCode, List<Long> cartIds) {
        Order temporaryOrder = createOrder(userCode, cartIds);

        // 임시 주문 데이터 DTO 생성 (주문 객체 + 장바구니 ID 목록)
        TempOrderDto temporaryOrderDto = TempOrderDto.builder()
                .order(temporaryOrder)
                .cartIds(cartIds)
                .build();

        String orderKey = RedisProperties.Order.KEY_PREFIX + userCode;

        // Redis에 임시 주문 정보 저장
        redisRepository.saveHash(orderKey, RedisProperties.Order.DATA_FIELD, temporaryOrderDto, RedisProperties.Order.EXPIRATION);
    }

    /**
     * 주문 완료 처리
     */
    public OrderDto completeOrder(Long userCode, OrderDto orderDto) {
        String orderKey = RedisProperties.Order.KEY_PREFIX + userCode;

        // Redis에서 임시 주문 정보 조회
        TempOrderDto temporaryOrder = redisRepository.findHash(orderKey, RedisProperties.Order.DATA_FIELD);
        if (temporaryOrder == null) {
            throw new EntityNotFoundException(HttpStatus.NOT_FOUND,ErrorCode.ORDER_NOT_FOUND);
        }

        // 주문 완료 처리
        Order completedOrder = orderConfirm(temporaryOrder.getOrder(), orderDto);

        return OrderDto.from(completedOrder);
    }

    /**
     * 주문서 화면에 나타날 정보 (사용자에게 입력받지 않고 자동으로 가져와 화면에 띄워주거나 저장할 값)
     * @param cartIds card id 리스트
     * @return order 객체 반환
     */
    private Order createOrder(Long userCode, List<Long> cartIds) {
        List<Cart> carts = cartRepository.findAllById(cartIds);

        boolean sameUser = carts.stream().allMatch(cart -> cart.getUser().getUserCode().equals(userCode));
        if (!sameUser) {
            throw new IllegalArgumentException("장바구니의 사용자 정보가 일치하지 않습니다.");
        }

        User user = userRepository.findByUserCode(userCode);

        Order order = Order.builder()
                .user(user)
                .productNames(getProductNames(carts))
                .totalPrice(calculateTotalPrice(carts))
                .build();

        for (Cart cart : carts) {
            OrderItem orderItem = createOrderItemFromCart(cart, order);
            order.addOrderItem(orderItem);
        }

        return order;
    }


    /**
     * 주문 테이블 저장
     * @param temporaryOrder 세션에 저장된 주문서
     * @param orderDto 사용자에게 입력받은 주문 정보
     * @return 주문 테이블 저장
     */
    private Order orderConfirm(Order temporaryOrder, OrderDto orderDto) {
        //주문번호 생성
        String merchantUid = generateMerchantUid();
        // 임시 주문서와 사용자에게 입력받은 정보 합치기
        temporaryOrder.orderConfirm(merchantUid, orderDto);
        // 주문 총 가격 재계산
        temporaryOrder.calculateTotalPrice();
        return orderRepository.save(temporaryOrder);
    }

    // 주문 상품 이름들을 가져오는 메서드
    private String getProductNames(List<Cart> carts) {
        return carts.stream()
                .map(cart -> cart.getProductManagement().getProduct().getProductName())
                .collect(Collectors.joining(", "));
    }

    // 총 가격을 계산하는 메서드
    private BigDecimal calculateTotalPrice(List<Cart> carts) {
        BigDecimal totalPrice = BigDecimal.ZERO;
        for (Cart cart : carts) {
            // 단가에 수량을 곱하여 합산
            BigDecimal itemPrice = BigDecimal.valueOf(cart.getProductManagement().getProduct().getPrice());
            BigDecimal quantity = BigDecimal.valueOf(cart.getQuantity());
            totalPrice = totalPrice.add(itemPrice.multiply(quantity));
        }
        return totalPrice;
    }

    // 주문번호 생성 메서드
    private String generateMerchantUid() {
        // 현재 날짜와 시간을 포함한 고유한 문자열 생성
        String uniqueString = UUID.randomUUID().toString().replace("-", "");
        // 무작위 문자열과 현재 날짜/시간을 조합하여 주문번호 생성
        return LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd")) +
                '-' + uniqueString;
    }

    // Cart －＞ OrderItem 생성 메서드
    private OrderItem createOrderItemFromCart(Cart cart, Order order) {
        ProductManagement productMgt = cart.getProductManagement();
        Product product = productMgt.getProduct();

        // 개별 상품 가격
        BigDecimal itemPrice = BigDecimal.valueOf(product.getPrice());

        return OrderItem.builder()
                .order(order)
                .productManagement(productMgt)
                .quantity(cart.getQuantity())
                .price(itemPrice) // 단위 가격
                .build();
    }

}