import React, { useEffect } from 'react';
import { useNavigate } from 'react-router-dom';
import { useKakaoLogin, useUserData } from './authHooks';
import { setCookie } from '../util/cookie';

const LoginHandler = (props) => {
    const navigate = useNavigate();
    const code = new URL(window.location.href).searchParams.get("code");

    const { mutate: kakaoLogin, isLoading: isLoggingIn, isError: loginError } = useKakaoLogin(code);
    const { data: userData, isLoading: isUserDataLoading, isError: userDataError, refetch: refetchUserData } = useUserData();

    useEffect(() => {
        if (code) {
            kakaoLogin(undefined, {
                onSuccess: () => {
                    refetchUserData();
                },
                onError: (error) => {
                    console.error('카카오로그인 중 오류 발생:', error);
                }
            });
        }
    }, [code, kakaoLogin, refetchUserData]);

    useEffect(() => {
        if (userData) {
            setCookie("id", userData.userCode);
            navigate("/");
            window.location.reload();
        }
    }, [userData, navigate]);

    if (isLoggingIn || isUserDataLoading) {
        return (
            <div className="LoginHandler">
                <div className="notice">
                    <p>로그인 중입니다.</p>
                    <p>잠시만 기다려주세요.</p>
                    <div className="spinner"></div>
                </div>
            </div>
        );
    }

    if (loginError || userDataError) {
        return (
            <div className="LoginHandler">
                <div className="notice">
                    <p>로그인 중 오류가 발생했습니다.</p>
                    <p>다시 시도해 주세요.</p>
                </div>
            </div>
        );
    }

    return null;
};

export default LoginHandler;
