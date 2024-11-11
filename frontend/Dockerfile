# 빌드 단계
FROM node:20.16.0-alpine AS build
WORKDIR /app

# 의존성 설치 및 빌드(CI)
COPY package.json .
RUN npm install

# 빌드 시 환경 변수 설정
COPY . .
ARG REACT_APP_EC2_HOST_URL
ENV REACT_APP_EC2_HOST_URL=$REACT_APP_EC2_HOST_URL

RUN npm run build

# Nginx를 기반으로 하는 최종 이미지
FROM nginx:alpine

# Nginx 설정 파일 복사
COPY nginx/nginx.conf /etc/nginx/nginx.conf

# mime.types 파일을 복사
COPY nginx/mime.types /etc/nginx/mime.types

# 빌드된 리액트 앱을 Nginx의 HTML 디렉토리로 복사
COPY --from=build /app/build /usr/share/nginx/html

# 포트 설정
EXPOSE 80

# Nginx 실행
CMD ["nginx", "-g", "daemon off;"]

