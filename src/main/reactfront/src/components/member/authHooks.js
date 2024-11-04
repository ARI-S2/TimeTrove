import { useQuery, useMutation } from "react-query";
import apiClient from '../../http-commons';
import { getCookie, setCookie } from '../util/cookie';

const fetchKakaoLogin = async (code) => {
    const response = await apiClient.get(`/login/oauth2/callback/kakao?code=${code}`);
    const tokens = response.data;
    setCookie('jwtToken', tokens);
    return tokens;
};

export const fetchUser = async () => {
    const response = await apiClient.get('/user');
    return response.data;
};

export const useKakaoLogin = (code) => {
    return useMutation(() => fetchKakaoLogin(code));
};

export const useUserData = () => {
    return useQuery('userInfo', fetchUser, {
        enabled: !!getCookie('jwtToken'), // 토큰이 유효할 때만 수행
    });
};
