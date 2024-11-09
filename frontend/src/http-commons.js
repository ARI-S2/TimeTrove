import axios from "axios";
import {getCookie, logout, setCookie} from "./components/util/cookie";
import {KAKAO_AUTH_URL} from "./components/member/OAuth";

// axios 인스턴스 생성
const instance = axios.create({
    baseURL: `http://${process.env.REACT_APP_EC2_HOST_URL}/api`,
    //baseURL: `http://localhost/api`,
    headers: {
        "Content-Type": "application/json;charset=utf-8",
        "Access-Control-Allow-Origin": "*"
    },
    withCredentials: true,
});

// 요청 인터셉터 추가
instance.interceptors.request.use(
    (config) => {
        // JWT 토큰을 가져옴
        const tokens = getCookie('jwtToken');
        // 토큰이 있을 때만 헤더에 추가
        if (tokens && tokens.accessToken) {
            config.headers['Authorization'] = `Bearer ${tokens.accessToken}`;
        }
        return config;
    },
    (error) => {
        return Promise.reject(error);
    }
);

instance.interceptors.response.use(
    (response) => {
        return response;
    },
    async (error) => {
        const originalRequest = error.config;

        if (error.response) {
            const { status, data } = error.response;
            // AccessToken 만료 시 RefreshToken으로 토큰 재발급 시도
            if (status === 401 && data.code === 'EXPIRED_TOKEN' && !originalRequest._retry) {
                originalRequest._retry = true;
                const tokens = getCookie('jwtToken');

                try {
                    const response = await axios.post(`http://${process.env.REACT_APP_EC2_HOST_URL}/api/reissue`, null, {
                    //const response = await axios.post(`http://localhost/api/reissue`, null, {
                        headers: {
                            'Authorization': `${tokens.refreshToken}`,
                        },
                    });
                    const newTokens = response.data;
                    setCookie('jwtToken', newTokens);
                    originalRequest.headers['Authorization'] = `Bearer ${newTokens.accessToken}`;

                    // 기존 요청 재시도
                    return instance(originalRequest);
                } // RefreshToken 만료 or 유효성 오류 발생 시 로그아웃 후 재로그인
                catch (reissueError) {
                    console.log(reissueError.response.data);
                    if (reissueError.response && reissueError.response.data.code === '103_INVALID_REFRESH_TOKEN') {
                        alert('재로그인이 필요합니다.');
                        logout();
                        // 로그인 페이지로 이동
                        window.location.href = KAKAO_AUTH_URL;
                    }
                    return Promise.reject(reissueError);
                }
            }
            // AccessToken 유효성 오류 발생 시 로그아웃 후 재로그인
            if (status === 401 && data.code === 'INVALID_TOKEN') {
                alert('재로그인이 필요합니다.');
                logout();

                // 로그인 페이지로 이동
                window.location.href = KAKAO_AUTH_URL;
                return Promise.reject(error);
            }
        }

        return Promise.reject(error);
    }
);

export default instance;