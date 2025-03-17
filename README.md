# TIMETROVE
![mediamodifier_image_draphed_01_2000x1333](https://github.com/user-attachments/assets/b9498207-32ac-48dd-a15d-ea4d3eda9321)

>  개인 프로젝트
> 
> 2024.7. – 2024.12.

<br>

## 📝 프로젝트 개요
평소 관심 있던 시계 상품의 데이터를 크롤링하며 쌓아온 경험을 토대로, 직접 시계 쇼핑몰을 만들어보기로 했습니다. 쇼핑몰은 웹의 핵심 기능을 두루 다뤄야 하기에, 다양한 기술을 폭넓게 경험할 좋은 기회라고 생각했습니다. 기획부터 프론트엔드·백엔드 개발, CI/CD 구축까지 전 과정을 혼자서 맡아 진행하며, 모든 결정을 직접 내리는 과정이 고민의 연속이었지만 그만큼 깊이 있는 배움을 얻을 수 있었습니다.

<br>

## ⚙️ 사용 기술
- `Gradle`, `Java 17`, `Spring Boot 3`, `Spring Security 6`
- `MySQL 8`, `JPA`, `Querydsl`, `Redis`
- `React`, `JavaScript`
- `AWS EC2`, `Doker`, `Github Actions`

<br>

## 🚀 CI/CD 시스템 아키텍처
![timetrove_AWS](https://github.com/user-attachments/assets/fe99363b-4c77-4277-950a-7d60ad7b6120)

<br>

## 📌 기능 소개

### 1. 사용자 인증 및 권한 관리
- JWT와 OAuth 2.0을 활용한 소셜 로그인 구현
- Redis를 이용한 Refresh Token 관리로 사용자 세션 유지 및 보안 강화
- Spring Security를 통한 엔드포인트 보안 및 사용자 권한 관리

### 2. 시계 쇼핑
- 시계 목록 조회 및 상세보기
- 동적 검색 및 필터링 기능 (Querydsl 활용)
- 비관적 락을 통한 동시성 제어로 안정적인 재고 관리
- 장바구니 및 포트원 API 구매

### 3. 게시판 및 댓글 
- 게시글 CRUD 기능
- 계층형 댓글 구조 지원 (대댓글 기능)
- Querydsl Fetch Join을 통한 N+1 문제 해결 및 성능 최적화

### 4. 마이페이지 
- 사용자 정보 조회 및 수정 기능
- 사용자별 주문 내역 조회
- 최근 작성 댓글 목록

### 5. 캐싱 및 성능 최적화
- Redis를 활용한 사용자 정보 캐싱
- 실시간 랭킹 정보를 위한 Redis Sorted Set 활용
- 상품명 및 정렬 기준 컬럼에 인덱스 적용으로 검색 성능 향상
- React-Query로 클라이언트 측에 상품 목록을 캐싱해 API 호출 최소화
  
### 6. 실시간 랭킹 기능
- 조회수 기반 인기 상품 랭킹 기능
- Hacker News Ranking 알고리즘을 활용한 실시간 인기글 기능

### 7. 예외 처리 및 에러 관리
- 커스텀 예외 처리를 통한 에러 관리
- JWT 관련 예외 처리로 안전한 토큰 관리

<br>

## 🔍 트러블 슈팅 및 고민
### [동시성 문제로 인한 재고감소 오류 해결]
<details>
<summary> 자세히 알아보기 </summary>
<div markdown="1">

## 💡 문제 상황
상품 구매 시 재고 감소에 대한 동시성 문제가 발생했습니다.
- 100명의 사용자가 동시에 1개의 상품을 구매하는 테스트 실행
- 예상되는 결과: 상품 재고 100개 감소
- 실제 결과: 상품 재고 11개만 감소

```java
@Test
public void testConcurrentPurchase() throws InterruptedException {
    ExecutorService executorService = Executors.newFixedThreadPool(USER_COUNT);
    CountDownLatch latch = new CountDownLatch(USER_COUNT);

    for (int i = 0; i < USER_COUNT; i++) {
        final long userCode = i;
        executorService.submit(() -> {
            try {
                List<CartDto> cartDtoList = cartRepository
                    .findByUser_UserCodeAndPurchasedFalse(userCode)
                    .stream()
                    .map(CartDto::convertCartToDto)
                    .toList();

                cartService.processPurchase(cartDtoList);
            } finally {
                latch.countDown();
            }
        });
    }
    latch.await();
}
```

### 원인 분석
동시에 들어온 여러 트랜잭션이 동일한 재고를 조회하고 업데이트하면서 발생하는 `Lost Update` 문제
  
## 🛠 Try 1 - Synchronized 키워드 적용
### 시도한 방법
- 상품 재고 감소 메서드에 `synchronized` 키워드 적용
```java
public synchronized void decreaseWatchQuantity(Long id, Long quantity) {
    Watch watch = watchRepository.findById(id)
        .orElseThrow(() -> new EntityNotFoundException(HttpStatus.NOT_FOUND, ErrorCode.WATCH_NOT_FOUND));
    watch.decreaseQuantity(quantity);
    watchRepository.save(watch);
}
```

### 결과
- ❌ 실패
- 원인: `@Transactional`과 `synchronized`의 동작 방식 차이
  - `synchronized`로 스레드 동기화를 하더라도 실제 DB 반영은 트랜잭션 종료 시점에 발생
  - 단일 서버에서만 동작하는 한계

## 🛠 Try 2 - 낙관적 락(Optimistic Lock)
### 시도한 방법
1. 엔티티에 버전 정보 추가
```java
@Entity
public class Watch {
    @Version
    private Long version;
    ...
}
```

2. 낙관적 락 적용
```java
@Lock(LockModeType.OPTIMISTIC)
@Query("SELECT w FROM Watch w WHERE w.id = :id")
Watch findByIdWithOptimisticLock(@Param("id") Long id);
```

3. 재시도 로직 구현 (`@Retryable` 사용)
```java
@Transactional
@Retryable(
    value = ObjectOptimisticLockingFailureException.class,
    maxAttempts = 30,
    backoff = @Backoff(delay = 50)
)
public void processPurchase(List<CartDto> cartDtoList) {
    // 구매 로직
}
```

### 결과
- ✅ 성공
- 단점: 충돌 발생 시 재시도로 인한 성능 저하

## 🛠 Try 3 - 비관적 락(Pessimistic Lock)
### 시도한 방법
```java
@Lock(LockModeType.PESSIMISTIC_WRITE)
@QueryHints({@QueryHint(name = "jakarta.persistence.lock.timeout", value = "1000")})
@Query("SELECT w FROM Watch w WHERE w.id = :id")
Watch findByIdWithPessimisticLock(@Param("id") Long id);
```

### 결과
- ✅ 성공
- 장점: 동시성 문제 해결에 가장 안정적
- 단점: 동시 요청이 많을 경우 성능 저하 가능성

## 🛠 Try 4 - Redis 분산 락
### 시도한 방법
1. Redisson 설정
```java
@Configuration
public class RedisConfig {
    @Bean
    public RedissonClient redissonClient() {
        Config config = new Config();
        config.useSingleServer()
            .setAddress("redis://" + host + ":" + port)
            .setPassword(password);
        return Redisson.create(config);
    }
}
```

2. 분산 락 구현
```java
public void processPurchase(List<CartDto> cartDtoList) {
    String key = REDISSON_LOCK_PREFIX + cartDtoList.get(0).getWatchId();
    RLock lock = redisson.getLock(key);
    
    try {
        boolean available = lock.tryLock(10, 1, TimeUnit.SECONDS);
        if (!available) {
            log.info("Redisson Lock 획득 실패");
            return;
        }
        cartService.processPurchase(cartDtoList);
    } catch (InterruptedException e) {
        Thread.currentThread().interrupt();
    } finally {
        lock.unlock();
    }
}
```

### 결과
- ✅ 성공
- 장점: 분산 환경에서 효과적
- 단점: 단일 서버 환경에서는 오버헤드 발생 가능성

## 🎯 최종 해결책
최종적으로 **비관적 락(Pessimistic Lock)** 방식을 채택했습니다.

### 선택 이유
1. 현재 단일 서버, 단일 DB 환경 운영
2. 동일 상품에 대한 동시 구매 요청이 빈번하게 발생
3. 데이터 정합성이 매우 중요한 재고 시스템
4. 락 획득 실패 시 재시도 로직이 필요 없음

이러한 근거를 고려했을 때, 현재 시스템에서는 비관적 락이 가장 적합한 해결책이라고 판단했습니다.

### 학습 내용

1. 프로세스와 스레드의 차이
2. 비관적 락과 낙관적 락의 차이점 및 적용 상황
3. 분산 환경에서의 락 구현 방식
4. 성능과 데이터 일관성 사이의 균형 고려의 중요성

</div>
</details>


### [멱등성 키를 활용하여 중복 결제 방지하기]
<details>
<summary> 자세히 알아보기 </summary>
<div markdown="1">
  
![Image](https://github.com/user-attachments/assets/7089e9ee-271e-4300-b4db-2a00bd0ef3fa)
  
</div>
</details>

### [Redis TTL을 활용하여 Refresh Token이 DB에 쌓이는 문제 방지]
<details>
<summary> 자세히 알아보기 </summary>
<div markdown="1">

## 💡 문제 상황
Refresh Token을 데이터베이스에 저장하고 관리하는 과정에서 다음과 같은 문제점이 발생했습니다:

1. 만료된 Refresh Token이 데이터베이스에 계속 쌓이는 현상
2. 토큰 조회와 갱신 시 데이터베이스에 과도한 부하 발생

### 원인 분석

1. 사용자가 명시적으로 로그아웃하지 않은 경우, 만료된 Refresh Token을 자동으로 삭제하는 메커니즘 부재
2. 토큰 관련 작업이 빈번하게 발생하면서 데이터베이스 성능에 영향을 미침

### 🛠 해결 과정

1. **Redis 도입**
   - In-memory 데이터 저장소인 Redis를 사용하여 Refresh Token 관리
   - TTL(Time To Live) 기능을 활용하여 토큰 자동 만료 구현

2. **Refresh Token 구현**
   - UUID 형식의 Refresh Token 사용 (서버에서 완전한 제어 가능)

3. **Redis 설정 및 구현**
   - RedisTemplate을 사용하여 Redis 연결 및 데이터 직렬화 설정
   - RedisRepository 클래스 구현으로 Redis 작업 공통화

4. **토큰 재발급 프로세스 구현**
   - AuthController에서 토큰 재발급 요청 처리
   - AuthService에서 Redis를 통한 Refresh Token 검증 및 재발급 로직 구현

5. **예외 처리 개선**
   - JwtRequestFilter에서 토큰 관련 예외 캐치 및 request에 저장
   - CustomAuthenticationEntryPoint를 통해 인증 실패 예외 처리
   - CustomExceptionHandler로 전역적인 예외 처리 구현

6. **프론트엔드 토큰 관리**
   - Axios 인터셉터를 활용한 자동 토큰 갱신 및 예외 처리 구현

### 🎯 최종 해결책

Redis의 TTL 기능을 활용하여 Refresh Token을 관리하고, 예외 처리를 개선하여 보안성과 사용자 경험을 향상시켰습니다.

### 구현 코드

```java
@Configuration
public class RedisConfig {
    @Bean
    public RedisTemplate<String, Object> redisTemplate() {
        RedisTemplate<String, Object> redisTemplate = new RedisTemplate<>();
        redisTemplate.setKeySerializer(new StringRedisSerializer());
        redisTemplate.setValueSerializer(new GenericJackson2JsonRedisSerializer());
        redisTemplate.setConnectionFactory(redisConnectionFactory());
        return redisTemplate;
    }
}

@Repository
public class RedisRepository {
    private final HashOperations<String, String, Object> hashOperations;

    public <T> void saveHash(final String key, final String field, final T value, final Long duration) {
        hashOperations.put(key, field, value);
        redisTemplate.expire(key, duration, TimeUnit.SECONDS);
    }
}

@Transactional
public TokenDto reIssueTokens(String refreshToken) {
    Long userCode = redisRepository.findHash("refresh-token", refreshToken);
    if (userCode == null) {
        throw new CustomException(HttpStatus.UNAUTHORIZED, ErrorCode.INVALID_REFRESH_TOKEN);
    }
    String newAccessToken = createAccessToken(userRepository.findByUserCode(userCode));
    String newRefreshToken = createRefreshToken(userCode);
    deleteRefreshToken(refreshToken);
    return new TokenDto(newAccessToken, newRefreshToken);
}
```

### 개선된 점

1. 만료된 Refresh Token 자동 삭제로 데이터베이스 관리 부담 감소
2. 빠른 읽기/쓰기로 인한 성능 향상
3. 토큰 관리의 유연성 증가 (즉시 폐기 가능)
4. 프론트엔드에서의 자동 토큰 갱신으로 사용자 경험 개선

### 학습 내용

1. Redis의 특성과 활용 방법
2. JWT와 UUID 형식의 토큰 비교 및 선택 기준
3. 스프링 시큐리티와 JWT를 이용한 인증 구현
4. 프론트엔드와 백엔드의 통합적인 예외 처리 방법

</div>
</details>

### [복합 인덱스로 구매 내역 조회 성능 개선하기]
<details>
<summary> 자세히 알아보기 </summary>
<div markdown="1">
  
![Image](https://github.com/user-attachments/assets/3720d9b7-d612-4f45-8782-abcf33589284)
  
</div>
</details>

<br>

## 🖥️ 화면 설계
<details>
<summary> 자세히 알아보기 </summary>
<div markdown="1">

### [메인 페이지]
![제목을-입력해주세요_-001](https://github.com/user-attachments/assets/d224643e-1199-41df-a60e-dc33aae49bea)

### [상품 페이지]
![Timetrove_상품화면설계](https://github.com/user-attachments/assets/a5eeb105-bf84-4822-a258-aa1fb62565b8)

<br>

### [게시판 페이지]
![Timetrove_게시판화면설계](https://github.com/user-attachments/assets/7433b71a-0b2c-4d7b-a3a9-25bb4e1d4b05)

### [마이 페이지]
![Timetrove_마이페이지화면설계](https://github.com/user-attachments/assets/46fc7206-d9b8-42c0-982c-152b8588a1ee)

</div>
</details>
