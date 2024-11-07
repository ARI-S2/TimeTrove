# 빌드 단계: Builder Stage
FROM openjdk:17.0-slim AS builder
WORKDIR /app

# 소스 복사 및 빌드 실행
COPY . .
RUN ./gradlew build --no-daemon

# 실행 단계: Production Image
FROM openjdk:17.0-slim
WORKDIR /app

# 시간대 설정
ENV TZ=Asia/Seoul

# 빌드된 JAR 파일 복사 (환경 변수로 설정)
ARG JAR_FILE=build/libs/*.jar
COPY --from=builder /app/${JAR_FILE} app.jar

# JAR 파일 실행
ENTRYPOINT ["java", "-jar", "app.jar"]

# FROM : Docker Base Image (기반이 되는 이미지, <이미지 이름>:<태그> 형식으로 설정), # openjdk 17 버전을 사용
# ARG : 컨테이너 내에서 사용할 수 있는 변수를 지정할 수 있다.
# COPY : 위에 선언했던 JAR_FILE 변수를 컨테이너의 app.jar로 복사한다.
# ENTRYPOINT : 컨테이너가 시작되었을 때 스크립트 실행