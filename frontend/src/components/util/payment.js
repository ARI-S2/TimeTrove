import axios from "axios";
import { v4 as uuidv4 } from 'uuid'; // UUID 라이브러리 추가 필요
import { getCookie } from "./cookie";

// 멱등성 키 관리를 위한 저장소
const paymentRequestMap = new Map();

export const requestPay = (amount, item, nickname, axios_url, payload) => {
    // 요청마다 고유한 멱등성 키 생성
    const idempotencyKey = uuidv4();

    // 현재 시간 기반 merchant_uid 생성
    const merchantUid = `mid_${new Date().getTime()}`;

    // 이미 처리 중인 결제가 있는지 확인
    if (sessionStorage.getItem('isProcessingPayment') === 'true') {
        alert('이미 결제가 진행 중입니다. 잠시만 기다려주세요.');
        return;
    }

    // 결제 진행 중 상태 설정
    sessionStorage.setItem('isProcessingPayment', 'true');

    const { IMP } = window;
    IMP.init('imp71305537'); // 가맹점 번호 지정
    IMP.request_pay({
            pg : 'kakaopay', // 결제 방식 지정
            pay_method : 'card',
            merchant_uid: merchantUid,
            name : `${item}`,
            amount : `${amount}`,
            buyer_email : '구매자 이메일',
            buyer_name : `${nickname}`,
            buyer_tel : '010-1222-2222',
            buyer_addr : '서울특별시 강남구 삼성동',
            buyer_postcode : '123-456'
        }, async (rsp) => {
            try {
                if (rsp.success) {
                    // 결제 성공 시에만 처리
                    // 멱등성 키와 결제 정보를 맵에 저장
                    paymentRequestMap.set(idempotencyKey, {
                        merchantUid: merchantUid,
                        amount: amount,
                        timestamp: new Date().getTime()
                    });

                    // 결제 정보에 멱등성 관련 데이터 추가
                    const payloadWithIdempotency = {
                        ...payload,
                        idempotencyKey: idempotencyKey,
                        merchantUid: merchantUid
                    };

                    // 백엔드 API 기본 URL 가져오기
                    const getBaseUrl = () => {
                        const hostUri = process.env.REACT_APP_EC2_HOST_URI;
                        if (hostUri) {
                            return `http://${hostUri}/api`;
                        }
                        return 'http://localhost/api';  // 개발 환경
                    };

                    // JWT 토큰 가져오기
                    const tokens = getCookie('jwtToken');

                    const response = await axios({
                        method: 'post',
                        url: `${getBaseUrl()}${axios_url}`,
                        data: payloadWithIdempotency,
                        headers: {
                            'Content-Type': 'application/json;charset=utf-8',
                            'Idempotency-Key': idempotencyKey,
                            'Authorization': tokens && tokens.accessToken ? `Bearer ${tokens.accessToken}` : '',
                        },
                        withCredentials: true
                    });

                    alert('구매가 완료되었습니다.');
                    window.location.href = '/mypage';
                } else {
                    // 결제 실패
                    alert(`결제에 실패했습니다: ${rsp.error_msg}`);
                }
            } catch (error) {
                console.error('Error while verifying payment:', error);

                // 응답 코드에 따른 처리
                if (error.response && error.response.status === 409) {
                    alert('이미 처리된 결제입니다. 마이페이지에서 확인해주세요.');
                    window.location.href = '/mypage';
                } else {
                    alert('구매 중 오류가 발생했습니다.');
                }
            } finally {
                // 결제 처리 완료 후 상태 초기화
                sessionStorage.removeItem('isProcessingPayment');

                // 일정 시간(10분) 후 멱등성 키 제거 설정
                setTimeout(() => {
                    paymentRequestMap.delete(idempotencyKey);
                }, 10 * 60 * 1000);
            }
        }
    );
};

// 멱등성 키 관리 함수
export const clearOldIdempotencyKeys = () => {
    const now = new Date().getTime();
    const expirationTime = 10 * 60 * 1000; // 10분(밀리초)

    for (const [key, value] of paymentRequestMap.entries()) {
        if (now - value.timestamp > expirationTime) {
            paymentRequestMap.delete(key);
        }
    }
};

// 주기적으로 오래된 멱등성 키 정리
setInterval(clearOldIdempotencyKeys, 60 * 1000); // 1분마다 실행