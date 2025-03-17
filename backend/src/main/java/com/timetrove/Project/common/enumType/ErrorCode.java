package com.timetrove.Project.common.enumType;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum ErrorCode {
    // 400: Bad Request Errors
    UNKNOWN("000_UNKNOWN", "알 수 없는 에러가 발생했습니다."),
    DUPLICATED_USER("001_DUPLICATED_USER", "이미 등록되어 있는 사용자입니다."),
    BLOCKED_USER("003_BLOCKED_USER", "차단된 사용자 입니다."),
    INVALID_USER_INFO("004_INVALID_USER_INFO", "사용자 정보 오류입니다."),

    // 401: Unauthorized Errors
    INVALID_TOKEN("100_INVALID_TOKEN", "유효하지 않은 토큰입니다."),
    EXPIRED_TOKEN("101_EXPIRED_TOKEN", "만료된 토큰입니다."),
    INVALID_REFRESH_TOKEN("102_INVALID_REFRESH_TOKEN", "유효하지 않은 리프레시 토큰입니다."),

    // 403: Forbidden Errors
    ACCESS_DENIED_USER("300_ACCESS_DENIED_USER", "접근 권한이 없는 사용자 요청입니다."),

    // 404: Not Found Errors
    USER_NOT_FOUND("400_USER_NOT_FOUND", "사용자를 찾을 수 없습니다."),
    PRODUCT_NOT_FOUND("401_PRODUCT_NOT_FOUND", "상품을 찾을 수 없습니다."),
    CART_NOT_FOUND("402_CART_NOT_FOUND", "장바구니를 찾을 수 없습니다."),
    BOARD_NOT_FOUND("403_BOARD_NOT_FOUND", "게시글을 찾을 수 없습니다."),
    COMMENT_NOT_FOUND("404_COMMENT_NOT_FOUND", "댓글을 찾을 수 없습니다."),
    ORDER_NOT_FOUND("405_ORDER_NOT_FOUND", "주문정보를 찾을 수 없습니다."),

    // 결제 관련 오류 코드
    PAYMENT_AMOUNT_MISMATCH("PAY_001", "결제 금액이 일치하지 않습니다."),
    MISSING_IDEMPOTENCY_KEY("PAY_002", "결제 요청에 멱등성 키가 필요합니다."),
    DUPLICATE_PAYMENT("PAY_003", "이미 처리된 결제 요청입니다.");

    private final String code;
    private final String msg;
}