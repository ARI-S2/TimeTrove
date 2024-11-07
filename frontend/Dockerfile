# 1. 빌드 단계: Node.js 환경에서 앱을 빌드
FROM node:20.16.0-alpine AS build
WORKDIR /usr/src/app

# package.json과 package-lock.json을 먼저 복사하고 의존성 설치
COPY package*.json ./
RUN npm install

# 소스 코드를 복사하고 빌드를 실행
COPY . .
RUN npm run build

# 2. 실행 단계: Nginx를 이용하여 정적 파일 서비스
FROM nginx:latest
EXPOSE 80
# 3000 대신 80 포트를 사용하여 Nginx에서 서비스하도록 설정

# Nginx 설정 파일을 복사
COPY ./default.conf /etc/nginx/conf.d/default.conf

# 빌드 스테이지에서 생성된 정적 파일을 Nginx의 기본 HTML 경로로 복사합니다.
COPY --from=build /usr/src/app/build /usr/share/nginx/html
