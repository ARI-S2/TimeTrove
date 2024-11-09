# 빌드 단계
FROM node:20.16.0-alpine AS build
WORKDIR /usr/src/app

COPY package*.json ./
RUN npm install  # 의존성 설치

# 빌드 시 환경 변수 설정
COPY . .
ARG REACT_APP_EC2_HOST_URL
ENV REACT_APP_EC2_HOST_URL=$REACT_APP_EC2_HOST_URL

RUN npm run build  # React 앱 빌드

# 실행 단계
FROM nginx:latest
EXPOSE 80

# Nginx 설정 파일 복사
COPY default.conf /etc/nginx/conf.d/frontend.conf
# 빌드된 React 앱 파일 복사
COPY --from=build /usr/src/app/build /usr/share/nginx/html