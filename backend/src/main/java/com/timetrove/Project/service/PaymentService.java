package com.timetrove.Project.service;

import com.siot.IamportRestClient.response.Payment;
import com.timetrove.Project.common.config.redis.RedisProperties;
import com.timetrove.Project.common.enumType.ErrorCode;
import com.timetrove.Project.common.enumType.PaymentStatus;
import com.timetrove.Project.common.exception.CustomException;
import com.timetrove.Project.common.exception.EntityNotFoundException;
import com.timetrove.Project.domain.*;
import com.timetrove.Project.dto.PaymentHistoryDto;
import com.timetrove.Project.dto.PaymentRequestDto;
import com.timetrove.Project.dto.TempOrderDto;
import com.timetrove.Project.repository.*;
import com.timetrove.Project.repository.redis.RedisRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class PaymentService {
    private final OrderRepository orderRepository;
    private final UserRepository userRepository;
    private final PaymentRepository paymentRepository;
    private final ProductManagementRepository productManagementRepository;
    private final CartRepository cartRepository;
    private final RedisRepository redisRepository;

    /**
     * 결제 처리 (멱등성 보장)
     * @param response 아임포트 결제 응답
     * @param paymentDto 결제 요청 DTO
     * @param userCode 사용자 코드
     * @param idempotencyKey 클라이언트가 제공한 멱등성 키
     */
    @Transactional(rollbackFor = Exception.class)
    public void processPaymentDone(Payment response, PaymentRequestDto paymentDto, Long userCode, String idempotencyKey) {
        // 멱등성 키가 없는 경우 예외 발생
        if (idempotencyKey == null || idempotencyKey.trim().isEmpty()) {
            throw new CustomException(HttpStatus.BAD_REQUEST, ErrorCode.MISSING_IDEMPOTENCY_KEY);
        }

        String redisKey = RedisProperties.Payment.IDEMPOTENCY_KEY_PREFIX + idempotencyKey;

        // 중복 요청 확인 - 이미 처리된 요청이면 즉시 리턴
        if (!redisRepository.setIdempotencyKey(redisKey, RedisProperties.Payment.IDEMPOTENCY_EXPIRATION)) {
            return;
        }

        try {
            Order currentOrder = orderRepository.findById(paymentDto.getOrderId())
                    .orElseThrow(() -> new EntityNotFoundException(HttpStatus.NOT_FOUND, ErrorCode.ORDER_NOT_FOUND));

            // 사용자 권한 확인
            if (!currentOrder.getUser().getUserCode().equals(userCode)) {
                throw new CustomException(HttpStatus.BAD_REQUEST, ErrorCode.INVALID_USER_INFO);
            }

            // 결제 금액 검증
            if (!response.getAmount().equals(BigDecimal.valueOf(paymentDto.getPrice()))) {
                throw new CustomException(HttpStatus.BAD_REQUEST, ErrorCode.PAYMENT_AMOUNT_MISMATCH,
                        "결제 금액이 일치하지 않습니다. 요청: " + paymentDto.getPrice() + ", 실제: " + response.getAmount());
            }

            // 재고 감소 처리
            decreaseStockWithPessimisticLock(currentOrder.getOrderItems());

            // 결제 상태 업데이트
            currentOrder.setPaymentStatus(PaymentStatus.SUCCESS);

            // 결제 내역 저장
            createPaymentHistory(response, paymentDto.getInventoryIdList(), currentOrder, userRepository.findByUserCode(userCode), paymentDto.getPrice());
        } catch (Exception e) {
            // 처리 중 오류 발생 시 멱등성 키 삭제 (재시도 가능하도록)
            redisRepository.deleteIdempotencyKey(idempotencyKey);
            throw e;
        }
    }

    /**
     * 주문 항목에 대한 재고 감소 처리 (비관적 락 적용)
     * @param orderItems 주문 항목 목록
     */
    private void decreaseStockWithPessimisticLock(List<OrderItem> orderItems) {
        for (OrderItem item : orderItems) {
            Long productManagementId = item.getProductManagement().getInventoryId();
            Long quantity = item.getQuantity();
            ProductManagement productMgt = productManagementRepository.findByIdWithPessimisticLock(productManagementId);

            try {
                productMgt.decreaseQuantity(quantity);
                productManagementRepository.save(productMgt);
            } catch (RuntimeException e) {
                throw e;
            }
        }
    }

    private void createPaymentHistory(Payment response, List<Long> productMgtIds, Order order, User user, Long totalPrice) {
        // 주문에 포함된 모든 상품에 대한 단일 결제 내역 생성
        ProductManagement firstProductMgt = productManagementRepository.findById(productMgtIds.get(0))
                .orElseThrow(() -> new EntityNotFoundException(HttpStatus.NOT_FOUND, ErrorCode.PRODUCT_NOT_FOUND));

        Product representativeProduct = firstProductMgt.getProduct();

        PaymentHistory paymentHistory = PaymentHistory.builder()
                .user(user)
                .order(order)
                .product(representativeProduct) // 대표 상품
                .productName(order.getProductNames()) // 모든 상품 이름
                .price(representativeProduct.getPrice()) // 대표 상품 가격
                .totalPrice(totalPrice) // 총 가격
                .impUid(response.getImpUid())
                .payMethod(response.getPayMethod())
                .statusType(PaymentStatus.SUCCESS)
                .bankCode(response.getBankCode())
                .bankName(response.getBankName())
                .buyerAddr(response.getBuyerAddr())
                .buyerEmail(response.getBuyerEmail())
                .build();

        paymentRepository.save(paymentHistory);
    }

    @Transactional(rollbackFor = Exception.class)
    public void completePaymentProcess(Long userCode) {
        String orderKey = RedisProperties.Order.KEY_PREFIX + userCode;

        // Redis에서 장바구니 ID 목록 조회
        TempOrderDto orderData = redisRepository.findHash(orderKey, RedisProperties.Order.DATA_FIELD);
        List<Long> cartIds = orderData.getCartIds();
        if (cartIds != null) {
            // 장바구니 항목 삭제
            cartIds.forEach(cartId -> {
                Cart cart = cartRepository.findById(cartId)
                        .orElseThrow(() -> new EntityNotFoundException(HttpStatus.NOT_FOUND, ErrorCode.CART_NOT_FOUND));
                cartRepository.delete(cart);
            });

            // 모든 작업이 완료된 후 Redis에서 임시 주문 데이터 삭제
            redisRepository.deleteHash(orderKey, RedisProperties.Order.DATA_FIELD);
        }
    }

    /**
     * 기간에 따른 결제 내역 조회
     * @param userCode 사용자 코드
     * @param period 조회 기간 (DAY, MONTH, YEAR, ALL)
     * @return 해당 기간의 결제 내역 리스트
     */
    public List<PaymentHistoryDto> getPaymentHistory(Long userCode, String period) {
        LocalDateTime startDate = null;
        LocalDateTime endDate = LocalDateTime.now();

        switch (period.toUpperCase()) {
            case "DAY":
                startDate = LocalDateTime.now().with(LocalTime.MIN);
                break;
            case "MONTH":
                startDate = LocalDateTime.now()
                        .withDayOfMonth(1)
                        .with(LocalTime.MIN);
                break;
            case "YEAR":
                startDate = LocalDateTime.now()
                        .minusYears(1)
                        .with(LocalTime.MIN);
                break;
            case "ALL":
            default:
                // 전체 내역은 startDate를 null로 유지
                return getAllPaymentHistory(userCode);
        }

        return getPaymentHistoryBetween(userCode, startDate, endDate);
    }

    /**
     * 전체 결제 내역 조회
     */
    private List<PaymentHistoryDto> getAllPaymentHistory(Long userCode) {
        List<PaymentHistory> paymentHistories = paymentRepository.findByUser_UserCodeOrderByPaidAtDesc(userCode);
        return PaymentHistoryDto.fromEntityList(paymentHistories);
    }

    /**
     * 기간 내 결제 내역 조회
     */
    private List<PaymentHistoryDto> getPaymentHistoryBetween(Long userCode, LocalDateTime start, LocalDateTime end) {
        List<PaymentHistory> paymentHistories = paymentRepository.findByUser_UserCodeAndPaidAtBetweenOrderByPaidAtDesc(
                userCode, start, end);

        return PaymentHistoryDto.fromEntityList(paymentHistories);
    }
}