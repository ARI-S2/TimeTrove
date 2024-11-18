# TIMETORVE
![TimeTrove_mainpage](https://github.com/user-attachments/assets/2faffa8f-65d5-48d8-a485-3fa696c3dc2a)

>  개인 프로젝트
> 
> 2024.6. – 2024.11. (6개월)

<br>

## 📝 프로젝트 개요
이 프로젝트는 사용자 경험을 최우선으로 고려한 커뮤니티 기반 시계 쇼핑몰입니다. 서비스의 기획부터 프론트엔드와 백엔드 개발, 그리고 CI/CD 구축을 통한 배포까지 전 과정을 경험하며, 종합적인 개발 역량을 쌓았습니다. 

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

### 2. 시계 상품 관리
- 시계 목록 조회 및 상세 정보 제공
- 동적 검색 및 필터링 기능 (Querydsl 활용)
- 비관적 락을 통한 동시성 제어로 안정적인 재고 관리

### 3. 게시판 및 댓글 
- 게시글 CRUD 기능
- 계층형 댓글 구조 지원 (대댓글 기능)
- Querydsl Fetch Join을 통한 N+1 문제 해결 및 성능 최적화

### 4. 장바구니 및 구매 
- 사용자별 장바구니 관리
- 상품 구매 프로세스 구현
- 동시성 제어를 통한 안전한 재고 관리

### 5. 마이페이지 
- 사용자 정보 조회 및 수정 기능
- 사용자별 주문 내역 및 활동 조회
- 최근 작성 댓글 목록 제공

### 6. 캐싱 및 성능 최적화
- Redis를 활용한 사용자 정보 캐싱
- 실시간 랭킹 정보를 위한 Redis Sorted Set 활용
- 상품명 및 정렬 기준 컬럼에 인덱스 적용으로 검색 성능 향상
- React-Query로 클라이언트 측에 상품 목록을 캐싱해 API 호출 최소화'
  
### 7. 실시간 랭킹 기능
- 판매량, 조회수 별 가중치를 부여한 인기 상품 랭킹 기능
- Hacker News Ranking 알고리즘을 활용한 실시간 인기글 기능

### 8. 예외 처리 및 에러 관리
- 커스텀 예외 처리를 통한 에러 관리
- JWT 관련 예외 처리로 안전한 토큰 관리
