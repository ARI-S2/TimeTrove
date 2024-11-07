# 빌드 단계: Builder Stage
FROM openjdk:17-jdk-slim as build

WORKDIR /app

# Gradle Wrapper와 빌드 파일 복사
COPY settings.gradle .
COPY backend/gradlew backend/
COPY backend/gradle backend/gradle
COPY backend/build.gradle backend/
COPY backend/src backend/src

WORKDIR /app/backend

# Gradle Wrapper에 실행 권한 부여
RUN chmod +x ./gradlew

# 빌드 실행
RUN ./gradlew build --no-daemon --warning-mode all --stacktrace

# 실행 단계: Production Image
FROM openjdk:17-jdk-slim

WORKDIR /app

# 빌드 단계에서 생성된 JAR 파일 복사
COPY --from=build /app/backend/build/libs/*.jar app.jar

# 애플리케이션 실행
ENTRYPOINT ["java", "-jar", "app.jar"]

# FROM : Docker Base Image (기반이 되는 이미지, <이미지 이름>:<태그> 형식으로 설정), # openjdk 17 버전을 사용
# ARG : 컨테이너 내에서 사용할 수 있는 변수를 지정할 수 있다.
# COPY : 위에 선언했던 JAR_FILE 변수를 컨테이너의 app.jar로 복사한다.
# ENTRYPOINT : 컨테이너가 시작되었을 때 스크립트 실행