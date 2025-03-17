package com.timetrove.Project.controller;

import com.siot.IamportRestClient.IamportClient;
import com.siot.IamportRestClient.exception.IamportResponseException;
import com.siot.IamportRestClient.response.IamportResponse;
import com.siot.IamportRestClient.response.Payment;
import com.timetrove.Project.dto.PaymentHistoryDto;
import com.timetrove.Project.dto.PaymentRequestDto;
import com.timetrove.Project.service.PaymentService;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.List;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
@Slf4j
public class PaymentController {
    private final PaymentService paymentService;
    private IamportClient iamportClient;

    @Value("${imp.api.key}")
    private String apiKey;
    @Value("${imp.api.secretkey}")
    private String secretKey;

    @PostConstruct
    public void init() {
        this.iamportClient = new IamportClient(apiKey, secretKey);
    }

    @PostMapping("/v1/order/payment/{imp_uid}")
    public IamportResponse<Payment> validateIamport(@PathVariable String imp_uid,
                                                    @RequestBody PaymentRequestDto paymentRequestDto,
                                                    @RequestHeader(value = "Idempotency-Key") String idempotencyKey)
            throws IamportResponseException, IOException {
        // 아임포트에서 결제 정보 조회
        IamportResponse<Payment> payment = iamportClient.paymentByImpUid(imp_uid);
        log.info("결제 요청 응답. 결제 내역 - 주문 번호: {}", payment.getResponse().getMerchantUid());

        // 결제 처리 - 클라이언트에서 제공한 멱등성 키 함께 전달
        paymentService.processPaymentDone(payment.getResponse(), paymentRequestDto, paymentRequestDto.getUserCode(), idempotencyKey);
        return payment;
    }

    // 결제 완료 화면에서 레디스의 임시 주문데이터, 장바구니 삭제하는 로직
    @GetMapping("/v1/order/paymentconfirm")
    public void confirmPayment(@AuthenticationPrincipal Long userCode) {
        paymentService.completePaymentProcess(userCode);
    }

    // 결제 내역 기간별 조회
    @Operation(summary = "최근 구매 내역 조회", description = "사용자가 최근에 구매한 상품 목록을 조회합니다.")
    @GetMapping("/purchase_history")
    public ResponseEntity<List<PaymentHistoryDto>> getPaymentHistory(
            @AuthenticationPrincipal Long userCode,
            @RequestParam(required = false, defaultValue = "ALL") String period) {

        List<PaymentHistoryDto> paymentHistory = paymentService.getPaymentHistory(userCode, period);
        return ResponseEntity.ok(paymentHistory);
    }
}
