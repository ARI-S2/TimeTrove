import { useQuery } from "react-query";
import apiClient from '../../http-commons'
import { useParams, Link, useNavigate  } from "react-router-dom";
import {useEffect, useState} from "react";
import {getCookie, setCookie} from "../util/cookie";
import {KAKAO_AUTH_URL} from "../member/OAuth";
import { requestPay } from "../util/payment";

function WatchDetail(){

    const {no} = useParams()
    const userId = getCookie('id');
    const [amount, setAmount] = useState(1);
    const navigate = useNavigate();


    const {isLoading,isError,error,data,refetch:watchDetail} = useQuery(['watch-detail',no],
        async () => {
            const response = await apiClient.get(`/watch/detail/${no}`)
            return response.data;
        }
    )
    useEffect(() => { watchDetail() },[ isLoading ])
    if(isLoading) return <h1 className="text-center">서버에서 전송 지연중...</h1>
    if(isError) return <h1 className="text-center">Error발생:{error}</h1>
    console.log(data)

    // 쿠키에 저장
    if (userId) {
        setCookie(userId + "_watch" + no, data.image);
    } else {
        setCookie("guest_watch" + no, data.image, { maxAge: 24 * 60 * 60 });
    }

    const handleIncrease = () => setAmount(prev => prev + 1);
    const handleDecrease = () => setAmount(prev => prev > 1 ? prev - 1 : 1);

    const cartDto = {
        watchNo: no,
        quantity: amount
    };

    const handleCartClick = async () => {
        if (!userId) {
            alert('로그인이 필요합니다.');
            navigate(KAKAO_AUTH_URL);
        } else {
            try {
                await apiClient.post('/mypage/cart', cartDto);
                alert('장바구니에 추가 성공');
                window.location.href = '/mypage';
            } catch (error) {
                console.error('Error while adding to cart:', error);
                alert('장바구니에 추가 실패');
            }
        }
    };

    const handlePurchaseClick = () => {
        if (!userId) {
            alert('로그인이 필요합니다.');
            navigate(KAKAO_AUTH_URL);
        } else {
            requestPay(totalPrice, data.name, userId, '/mypage/purchase-direct', cartDto);
        }
    };

    const totalPrice = data.s_price * amount;
    
    return (
        <div className="basic-N51" data-bid="ILlXsW0AmY">
            <div className="contents-inner">
                <div className="contents-container container-md">
                    <div className="contents-left">
                        <div className="contents-thumbnail">
                            <img className="contents-thumbimg"
                                 src={data.image} alt="썸네일이미지"/>
                        </div>
                        <ul className="contents-thumblist">
                            {data.dimages &&
                                data.dimages.map((dimage) =>
                                <li className="contents-thumbitem">
                                    <img className="contents-thumbimg"
                                         src={dimage} alt="썸네일이미지"/>
                                </li>
                                )}
                        </ul>
                    </div>
                    <div className="contents-right">
                        <div className="contents-right-group">
                            <div className="contents-brand">
                                <a href="javascript:void(0);">{data.model}</a>
                                <div className="contents-brand-group">
                                    <button className="contents-btn btn-like-line">
                                        <img src={process.env.PUBLIC_URL + "/icons/ico_like_black_line.svg"}
                                             alt="하트 라인 아이콘"/>
                                    </button>
                                    <button className="contents-btn btn-like-fill">
                                        <img src={process.env.PUBLIC_URL + "/icons/ico_like_black_fill.svg"}
                                             alt="하트 채워진 아이콘"/>
                                    </button>
                                    <button className="contents-btn">
                                        <img src={process.env.PUBLIC_URL + "/icons/ico_share_black.svg"}
                                             alt="공유 아이콘"/>
                                    </button>
                                </div>
                            </div>
                            <div className="textset">
                                <h2 className="textset-tit">{data.name}</h2>
                                <p className="textset-desc">
                                    세련된 디자인과 뛰어난 품질을 자랑하는 TimeTrove 시계는 착용자의 개성을 더욱 돋보이게 해주는 동시에, 시간의 소중함을 일깨워줍니다.
                                </p>
                            </div>
                            <p style={{
                                color: "grey",
                                textDecoration: "line-through",
                                fontSize: "larger"
                            }}>
                                {Number(data.c_price).toLocaleString()}
                            </p>
                            <p className="contents-price">{Number(data.s_price).toLocaleString()} <span>원</span>
                            </p>
                        </div>
                        <div className="contents-right-group">
                            <ul className="contents-right-list">
                                <li className="contents-right-item">
                                    <strong>할인율</strong>
                                    <span>{data.discount}</span>
                                </li>
                                <li className="contents-right-item">
                                    <strong>포인트적립</strong>
                                    {userId ? (
                                        <span>{data.points} 적립 예정</span>
                                    ) : (
                                        <span>로그인시 포인트적립이 가능합니다</span>
                                    )}
                                </li>
                                <li className="contents-right-item">
                                    <strong>배송정보</strong>
                                    <span>무료배송</span>
                                </li>
                                <li className="contents-right-item">
                                    <strong>판매자정보</strong>
                                    <span>TimeTrove</span>
                                </li>
                            </ul>
                        </div>
                        <div className="contents-right-group">
                            <ul className="contents-right-list">
                                <li className="contents-right-item">
                                    <strong>수량</strong>
                                    <div className="contents-amount">
                                        <button className="contents-btn btn-minus" type="button"
                                                onClick={handleDecrease}>
                                            <img src={process.env.PUBLIC_URL + "/icons/ico_minus_black.svg"}
                                                 alt="마이너스 아이콘"/>
                                        </button>
                                        <p className="contents-amount-num">{amount}</p>
                                        <button className="contents-btn btn-plus" type="button"
                                                onClick={handleIncrease}>
                                            <img src={process.env.PUBLIC_URL + "/icons/ico_plus_black.svg"}
                                                 alt="플러스 아이콘"/>
                                        </button>
                                    </div>
                                </li>
                            </ul>
                            <div className="contents-total">
                                <strong>총 결제금액</strong>
                                <p className="contents-price">{totalPrice.toLocaleString()} <span>원</span></p>
                            </div>
                        </div>
                        <div className="contents-btn-group">
                            <button className="btnset btnset-line" type="button" onClick={handleCartClick}>장바구니</button>
                            <button className="btnset" type="button" onClick={handlePurchaseClick}>구매하기</button>
                        </div>
                    </div>
                </div>
            </div>
            {data.dimages &&
                data.dimages.map((dimage) =>
                    <div className="image-container">
                        <img className="image-contents-thumbimg"
                             src={dimage} alt="썸네일이미지"/>
                    </div>
                )}
        </div>
    )
}

export default WatchDetail