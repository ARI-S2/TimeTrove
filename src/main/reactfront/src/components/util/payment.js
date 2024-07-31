import apiClient from "../../http-commons";


export const requestPay = (amount, item, nickname, axios_url, payload) => {
        const { IMP } = window;
        IMP.init('imp71305537'); // 가맹점 번호 지정
        IMP.request_pay({
                pg : 'kakaopay', // 결제 방식 지정
                pay_method : 'card',
                merchant_uid: `mid_${new Date().getTime()}`, // 현재 시간
                name : `${item}`,
                amount : `${amount}`, // 충전할 금액
                buyer_email : '구매자 이메일',
                buyer_name : `${nickname}`, // 충전 요청한 유저의 닉네임
                buyer_tel : '010-1222-2222',
                buyer_addr : '서울특별시 강남구 삼성동',
                buyer_postcode : '123-456'
            }, async (rsp) => {
                try {
                    await apiClient.post(axios_url, payload);
                    alert('구매가 완료되었습니다.');
                    window.location.href = '/mypage';
                } catch (error) {
                    console.error('Error while verifying payment:', error);
                    alert('구매 중 오류가 발생했습니다.');
                }
            }
        );
}