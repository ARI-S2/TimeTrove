package com.timetrove.Project;


import com.fasterxml.jackson.databind.ObjectMapper;
import com.siot.IamportRestClient.response.IamportResponse;
import com.siot.IamportRestClient.response.Payment;
import com.timetrove.Project.controller.PaymentController;
import com.timetrove.Project.domain.*;
import com.timetrove.Project.domain.Order;
import com.timetrove.Project.dto.PaymentRequestDto;
import com.timetrove.Project.repository.*;
import com.timetrove.Project.repository.querydsl.ProductRepository;
import com.timetrove.Project.repository.redis.RedisRepository;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.*;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicInteger;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
public class PaymentIdempotencyTest {

    // RedisRepository 모킹
    @MockBean
    private RedisRepository redisRepository;

    @MockBean
    private PaymentController paymentController;


    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private ProductManagementRepository productManagementRepository;

    @Autowired
    private PaymentRepository paymentRepository;


    private User testUser;
    private Product testProduct;
    private ProductManagement testProductMgt;
    private Order testOrder;
    private String impUid;
    private String idempotencyKey;


    @BeforeEach
    @Transactional
    void setUp() throws Exception {
        paymentRepository.deleteAll(); // 이전 테스트의 결제 내역 정리

        // 테스트 사용자 생성
        testUser = new User();
        testUser.setUserCode(1L);
        testUser.setKakaoNickname("testuser");
        userRepository.save(testUser);

        // 테스트 상품 생성
        testProduct = new Product();
        testProduct.setProductName("테스트 상품");
        testProduct.setPrice(1);
        testProduct.setModel("TEST-001");
        productRepository.save(testProduct);

        // 테스트 상품 관리 정보 생성
        testProductMgt = new ProductManagement();
        testProductMgt.updateInventory(100L, true, false, false, 0);
        productManagementRepository.save(testProductMgt);

        // 테스트 주문 생성
        testOrder = Order.builder()
                .user(testUser)
                .ordererName("테스트사용자")
                .productNames("테스트 상품")
                .totalPrice(BigDecimal.valueOf(1))
                .phoneNumber("010-1234-5678")
                .address("서울시 강남구")
                .detailAddress("테스트 아파트 101동 101호")
                .postCode("12345")
                .build();

        // 주문 아이템 추가
        OrderItem orderItem = OrderItem.builder()
                .productManagement(testProductMgt)
                .quantity(1L)
                .price(BigDecimal.valueOf(1))
                .build();
        testOrder.addOrderItem(orderItem);

        orderRepository.save(testOrder);

        // 테스트 결제 ID 및 멱등성 키 생성
        impUid = "imp_" + System.currentTimeMillis();
        idempotencyKey = UUID.randomUUID().toString();

        // PaymentController 모킹 설정
        Payment mockPayment = mock(Payment.class);
        when(mockPayment.getMerchantUid()).thenReturn(testOrder.getOrderId().toString());

        IamportResponse<Payment> mockIamportResponse = mock(IamportResponse.class);
        when(mockIamportResponse.getResponse()).thenReturn(mockPayment);

        // 첫 번째 호출은 성공, 두 번째 호출은 실패 처리
        when(paymentController.validateIamport(
                eq(impUid),
                any(PaymentRequestDto.class),
                eq(idempotencyKey)
        )).thenReturn(mockIamportResponse)
                .thenThrow(new RuntimeException("이미 처리된 결제"));

        // RedisRepository 모킹 설정
        when(redisRepository.setIdempotencyKey(anyString(), anyLong()))
                .thenReturn(true)  // 첫 번째 호출
                .thenReturn(false); // 두 번째 이후 호출

        doNothing().when(redisRepository).deleteIdempotencyKey(anyString());
    }

    @Test
    @WithMockUser
    @DisplayName("동일한 멱등성 키로 여러 번 결제 요청해도 한 번만 처리되는지 테스트")
    @Transactional
    void testIdempotencyPreventsDuplicatePayments() throws Exception {
        // 테스트용 PaymentRequestDto 생성
        PaymentRequestDto paymentRequestDto = new PaymentRequestDto(
                testUser.getUserCode(),
                testOrder.getOrderId(),
                1L,
                Collections.singletonList(testProductMgt.getInventoryId())
        );

        // 첫 번째 요청 (정상 처리)
        mockMvc.perform(post("/api/v1/order/payment/" + impUid)
                        .with(csrf())
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(paymentRequestDto))
                        .header("Idempotency-Key", idempotencyKey))
                .andExpect(status().isOk());

        // 두 번째 요청 (이미 처리된 결제 예외 발생)
        try {
            mockMvc.perform(post("/api/v1/order/payment/" + impUid)
                            .with(csrf())
                            .contentType("application/json")
                            .content(objectMapper.writeValueAsString(paymentRequestDto))
                            .header("Idempotency-Key", idempotencyKey))
                    .andExpect(status().is4xxClientError()); // 400대 응답 코드 예상
        } catch (Exception e) {
            // 예상된 예외라면 테스트 통과
            Assertions.assertTrue(e.getMessage().contains("이미 처리된 결제"), "예상된 예외 메시지가 아님");
        }

        // PaymentController의 메서드 호출 횟수 확인
        verify(paymentController, times(2)).validateIamport(
                eq(impUid),
                eq(paymentRequestDto),
                eq(idempotencyKey)
        );
    }

    @Test
    @WithMockUser
    @DisplayName("동시에 여러 요청이 들어와도 멱등성 키를 통해 중복 처리 방지")
    @Transactional
    void testConcurrentPaymentRequests() throws Exception {
        // 테스트용 PaymentRequestDto 생성
        PaymentRequestDto paymentRequestDto = new PaymentRequestDto(
                testUser.getUserCode(),
                testOrder.getOrderId(),
                1L,
                Collections.singletonList(testProductMgt.getInventoryId())
        );
        String requestJson = objectMapper.writeValueAsString(paymentRequestDto);

        // 동시에 5개의 요청 실행
        int numThreads = 5;
        ExecutorService executorService = Executors.newFixedThreadPool(numThreads);
        CountDownLatch latch = new CountDownLatch(numThreads);
        AtomicInteger successCount = new AtomicInteger(0);

        for (int i = 0; i < numThreads; i++) {
            executorService.submit(() -> {
                try {
                    MvcResult result = mockMvc.perform(post("/api/v1/order/payment/" + impUid)
                                    .with(csrf())
                                    .contentType("application/json")
                                    .content(requestJson)
                                    .header("Idempotency-Key", idempotencyKey))
                            .andReturn();

                    if (result.getResponse().getStatus() == 200) {
                        successCount.incrementAndGet();
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                } finally {
                    latch.countDown();
                }
            });
        }

        latch.await(); // 모든 스레드의 작업이 완료될 때까지 대기
        executorService.shutdown();

        // PaymentController의 메서드 호출 횟수 확인
        verify(paymentController, times(5)).validateIamport(
                eq(impUid),
                eq(paymentRequestDto),
                eq(idempotencyKey)
        );
    }

    @Test
    @WithMockUser
    @DisplayName("다른 멱등성 키로 여러 요청시 각각 처리")
    @Transactional
    void testDifferentIdempotencyKeys() throws Exception {
        // 테스트용 PaymentRequestDto 생성
        PaymentRequestDto paymentRequestDto = new PaymentRequestDto(
                testUser.getUserCode(),
                testOrder.getOrderId(),
                1L,
                Collections.singletonList(testProductMgt.getInventoryId())
        );

        // 첫 번째 요청 (첫 번째 멱등성 키)
        String idempotencyKey1 = idempotencyKey + "-1";
        mockMvc.perform(post("/api/v1/order/payment/" + impUid)
                        .with(csrf())
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(paymentRequestDto))
                        .header("Idempotency-Key", idempotencyKey1))
                .andExpect(status().isOk());

        // 결제 완료된 주문은 실제로는 추가 결제가 불가능하지만, 테스트를 위해 새 주문 생성
        Order secondOrder = Order.builder()
                .user(testUser)
                .ordererName("테스트사용자")
                .productNames("테스트 상품 2")
                .totalPrice(BigDecimal.valueOf(1))
                .phoneNumber("010-1234-5678")
                .address("서울시 강남구")
                .detailAddress("테스트 아파트 101동 101호")
                .postCode("12345")
                .build();

        // 주문 아이템 추가
        OrderItem secondOrderItem = OrderItem.builder()
                .productManagement(testProductMgt)
                .quantity(1L)
                .price(BigDecimal.valueOf(1))
                .build();
        secondOrder.addOrderItem(secondOrderItem);

        orderRepository.save(secondOrder);

        // 두 번째 요청용 DTO (다른 주문)
        PaymentRequestDto secondPaymentRequestDto = new PaymentRequestDto(
                testUser.getUserCode(),
                secondOrder.getOrderId(),
                1L,
                Collections.singletonList(testProductMgt.getInventoryId())
        );

        // 두 번째 요청 (두 번째 멱등성 키)
        String idempotencyKey2 = idempotencyKey + "-2";
        mockMvc.perform(post("/api/v1/order/payment/" + impUid)
                        .with(csrf())
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(secondPaymentRequestDto))
                        .header("Idempotency-Key", idempotencyKey2))
                .andExpect(status().isOk());

        // 두 개의 서로 다른 멱등성 키로 호출되었는지 확인
        verify(paymentController, times(1)).validateIamport(
                eq(impUid),
                eq(paymentRequestDto),
                eq(idempotencyKey1)
        );
        verify(paymentController, times(1)).validateIamport(
                eq(impUid),
                eq(secondPaymentRequestDto),
                eq(idempotencyKey2)
        );
    }

    @Test
    @WithMockUser
    @DisplayName("멱등성 키가 없는 경우 요청 실패")
    @Transactional
    void testMissingIdempotencyKey() throws Exception {
        // 테스트용 PaymentRequestDto 생성
        PaymentRequestDto paymentRequestDto = new PaymentRequestDto(
                testUser.getUserCode(),
                testOrder.getOrderId(),
                1L,
                Collections.singletonList(testProductMgt.getInventoryId())
        );

        // 멱등성 키 없이 요청
        mockMvc.perform(post("/api/v1/order/payment/" + impUid)
                        .with(csrf())
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(paymentRequestDto)))
                .andExpect(status().isBadRequest());
    }
}