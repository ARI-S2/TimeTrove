import axios from "axios";
import {getCookie} from "./components/util/cookie";

// axios 인스턴스 생성
const instance = axios.create({
    baseURL: "http://localhost",
    headers: {
        "Content-Type": "application/json;charset=utf-8",
        "Access-Control-Allow-Origin": "*"
    },
    withCredentials: true,  // 필요 시 설정
});

// 요청 인터셉터 추가
instance.interceptors.request.use(
    (config) => {
        // JWT 토큰을 가져옴
        const token = getCookie('jwtToken');
        if (token) {
            config.headers['Authorization'] = 'Bearer ' + token;
        }
        return config;
    },
    (error) => {
        return Promise.reject(error);
    }
);

// 응답 인터셉터 추가 (선택사항)
instance.interceptors.response.use(
    (response) => {
        return response;
    },
    (error) => {
        if (error.response && error.response.status === 401) {
            // 401 오류 처리 (예: 토큰 만료)
            // 로그아웃 처리 또는 토큰 갱신 로직을 여기에 추가
            console.log("인증 에러 발생");
        }
        return Promise.reject(error);
    }
);

export default instance;