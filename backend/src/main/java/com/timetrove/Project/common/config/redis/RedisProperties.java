package com.timetrove.Project.common.config.redis;

/**
 * Redis 관련 상수를 정의하는 인터페이스
 */
public interface RedisProperties {

	/**
	 * 주문 관련 Redis 속성
	 */
	interface Order {
		String KEY_PREFIX = "order:temp:";
		String DATA_FIELD = "order_data";
		Long EXPIRATION = 1800L; // 30분 (초 단위)
	}

	/**
	 * 상품 관련 Redis 속성
	 */
	interface Product {
		String POPULAR_KEY = "product:popular";
		String VIEW_COUNT_PREFIX = "product:view:";
		Long VIEW_COUNT_EXPIRATION = 86400L; // 24시간 (초 단위)
	}

	/**
	 * 사용자 관련 Redis 속성
	 */
	interface User {
		String SESSION_PREFIX = "user:session:";
		Long SESSION_EXPIRATION = 3600L; // 1시간 (초 단위)
	}

	/**
	 * 결제 관련 Redis 속성
	 */
	interface Payment {
		String IDEMPOTENCY_KEY_PREFIX = "payment:idempotency:";
		Long IDEMPOTENCY_EXPIRATION = 600L; // 10분 (초 단위)
	}

}