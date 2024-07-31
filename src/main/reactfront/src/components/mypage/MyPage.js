import React, { useState, useEffect } from 'react';
import { useQuery } from 'react-query';
import apiClient from '../../http-commons';
import CustomModal from '../CustomModal';
import EditInfoModal from './EditInfoModal';
import PurchaseHistoryModal from './PurchaseHistoryModal';
import CommentsListModal from './CommentsListModal';
import { fetchProfileImage } from '../util/api';
import {requestPay} from "../util/payment";

const MyPage = () => {
    const { data: user, error, isLoading, refetch: userRefetch } = useQuery('user', async () => {
        const response = await apiClient.get('/mypage');
        return response.data;
    });

    const [profileImageUrl, setProfileImageUrl] = useState(null);
    const [isEditInfoOpen, setEditInfoOpen] = useState(false);
    const [isPurchaseHistoryOpen, setPurchaseHistoryOpen] = useState(false);
    const [isCommentsListOpen, setCommentsListOpen] = useState(false);
    const [cartItems, setCartItems] = useState([]);
    const [totalPrice, setTotalPrice] = useState(0);

    useEffect(() => {
        if (user) {
            if (user.userinfo.kakaoProfileImg.startsWith('http://k.kakaocdn.net/')) {
                setProfileImageUrl(user.userinfo.kakaoProfileImg);
            } else {
                fetchProfileImage(user.userinfo.userCode).then(setProfileImageUrl);
            }
            const initialCartItems = (user.cartList || []).map(item => ({ ...item, checked: true }));
            setCartItems(initialCartItems);
        }
    }, [user]);

    useEffect(() => {
        calculateTotalPrice();
    }, [cartItems]);

    const calculateTotalPrice = () => {
        const total = cartItems
            .filter(item => item.checked)
            .reduce((acc, item) => acc + item.watchPrice * item.quantity, 0);
        setTotalPrice(total);
    };

    const handleIncrease = async (index) => {
        const newCartItems = [...cartItems];
        newCartItems[index].quantity += 1;
        setCartItems(newCartItems);
        console.log(newCartItems[index])

        try {
            await apiClient.put(`/mypage/cart/${newCartItems[index].id}`, { watchNo: newCartItems[index].watchNo, quantity: newCartItems[index].quantity });
        } catch (error) {
            console.error('Error updating cart item:', error);
        }
    };

    const handleDecrease = async (index) => {
        const newCartItems = [...cartItems];
        if (newCartItems[index].quantity > 1) {
            newCartItems[index].quantity -= 1;
            setCartItems(newCartItems);

            try {
                await apiClient.put(`/mypage/cart/${newCartItems[index].id}`, { watchNo: newCartItems[index].watchNo,  quantity: newCartItems[index].quantity });
            } catch (error) {
                console.error('Error updating cart item:', error);
            }
        }
    };

    const handleDelete = async (index) => {
        const cartItemId = cartItems[index].id;
        const newCartItems = cartItems.filter((_, i) => i !== index);
        setCartItems(newCartItems);

        try {
            await apiClient.delete(`/mypage/cart/${cartItemId}`);
        } catch (error) {
            console.error('Error deleting cart item:', error);
        }
    };

    const handleCheck = (index) => {
        const newCartItems = [...cartItems];
        newCartItems[index].checked = !newCartItems[index].checked;
        setCartItems(newCartItems);
    };

    const handlePurchase = async () => {
        const cartIds = cartItems
            .filter(item => item.checked)
            .map(item => item.id);

        try {
            requestPay(totalPrice, 'TimeTrove 시계', user.nickname, '/mypage/purchases', cartIds);
            // 구매 완료 후에 장바구니를 다시 불러오거나 다른 후속 작업을 수행할 수 있습니다.
            userRefetch();
        } catch (error) {
            console.error('Error purchasing items:', error);
            alert('구매 중 오류가 발생했습니다.');
        }
    };

    if (isLoading) {
        return <div>Loading...</div>;
    }

    if (error) {
        return <div>Error: {error.message}</div>;
    }

    return (
        <>
            <div className="campland-N23" data-bid="UFlYcY40ve">
                <div className="contents-inner">
                    <div className="contents-container">
                        <div className="textset textset-sub textset-center">
                            <h2 className="textset-tit">My Page</h2>
                        </div>
                        <ul className="contents-group contents-profile">
                            <li className="contents-profile-item">
                                <div className="imageset">
                                    <img className="imageset-img"
                                         src={profileImageUrl} alt="프로필 이미지"/>
                                </div>
                                <p className="contents-name">{user.userinfo.kakaoNickname}</p>
                            </li>
                            <li className="contents-profile-item">
                                <button onClick={() => setEditInfoOpen(true)} className="btnset btnset-round">내 정보 관리</button>
                            </li>
                        </ul>
                        <ul className="contents-group contents-benefit">
                            <li className="contents-benefit-item">
                                <a href="javascipt:void(0);" className="contents-benefit-link">
                                    <div className="iconset">
                                        <img className="iconset-icon"
                                             src={process.env.PUBLIC_URL + "/icons/ico_point_circle.svg"}
                                             alt="포인트"/>
                                    </div>
                                    <p className="contents-text">포인트</p>
                                </a>
                            </li>
                            <li className="contents-benefit-item">
                                <a onClick={() => setCommentsListOpen(true)} className="contents-benefit-link">
                                    <div className="iconset">
                                        <img className="iconset-icon"
                                             src={process.env.PUBLIC_URL + "/icons/ico_chat_circle.svg"}
                                             alt="내 댓글"/>
                                    </div>
                                    <p className="contents-text">내 댓글</p>
                                </a>
                            </li>
                            <li className="contents-benefit-item">
                                <a onClick={() => setPurchaseHistoryOpen(true)} className="contents-benefit-link">
                                    <div className="iconset">
                                        <img className="iconset-icon"
                                             src={process.env.PUBLIC_URL + "/icons/ico_star_circle.svg"}
                                             alt="구매내역"/>
                                    </div>
                                    <p className="contents-text">구매내역</p>
                                </a>
                            </li>
                        </ul>
                        <div className="contents-group contents-reservation">
                            <h2 className="contents-tit">
                                장바구니
                                <a href="javascipt:void(0);" className="contents-more">더보기</a>
                            </h2>
                            <hr/>
                            {cartItems &&
                                cartItems.map((cart, index) => (
                                    <div className="basic-N51" data-bid="ILlXsW0AmY" key={cart.id}>
                                        <div className="contents-inner" style={{padding: '1rem 1.6rem'}}>
                                            <div className="contents-container container-md">
                                                <div className="contents-left"
                                                     style={{width: '40%', alignContent: 'center'}}>
                                                    <input
                                                        type="checkbox"
                                                        checked={cart.checked}
                                                        onChange={() => handleCheck(index)}
                                                    />

                                                    <div className="contents-thumbnail" style={{height: 'auto'}}>
                                                        <img className="contents-thumbimg"
                                                             src={cart.watchImage} alt="썸네일이미지"/>
                                                    </div>
                                                </div>
                                                <div className="contents-right" style={{width: 'auto'}}>
                                                    {/* 삭제 버튼 추가 */}
                                                    <button
                                                        type="button"
                                                        onClick={() => handleDelete(index)}
                                                    >
                                                        삭제
                                                    </button>
                                                    <div className="contents-right-group">
                                                        <div className="contents-brand">
                                                            <a href="javascript:void(0);">{cart.watchModel}</a>
                                                        </div>
                                                        <div className="textset">
                                                            <h4>{cart.watchName}</h4>
                                                        </div>
                                                        <p className="contents-price">{Number(cart.watchPrice).toLocaleString()}
                                                            <span>원</span>
                                                        </p>
                                                    </div>
                                                    <div className="contents-right-group">
                                                        <ul className="contents-right-list">
                                                            <li className="contents-right-item">
                                                                <strong>수량</strong>
                                                                <div className="contents-amount">
                                                                    <button className="contents-btn btn-minus"
                                                                            type="button"
                                                                            onClick={() => handleDecrease(index)}>
                                                                        <img
                                                                            src={process.env.PUBLIC_URL + "/icons/ico_minus_black.svg"}
                                                                            alt="마이너스 아이콘"/>
                                                                    </button>
                                                                    <p className="contents-amount-num">{cart.quantity}</p>
                                                                    <button className="contents-btn btn-plus"
                                                                            type="button"
                                                                            onClick={() => handleIncrease(index)}>
                                                                        <img
                                                                            src={process.env.PUBLIC_URL + "/icons/ico_plus_black.svg"}
                                                                            alt="플러스 아이콘"/>
                                                                    </button>
                                                                </div>
                                                            </li>
                                                        </ul>
                                                        <div className="contents-total">
                                                            <strong>총 금액</strong>
                                                            <p className="contents-price">{(cart.watchPrice * cart.quantity).toLocaleString()}
                                                                <span>원</span></p>
                                                        </div>
                                                    </div>
                                                </div>
                                            </div>
                                        </div>
                                    </div>
                                ))}
                        </div>
                        <div className="contents-group contents-reservation">
                            <h2 className="contents-tit">
                                예상 결제금액
                            </h2>
                            <hr/>
                            <div className="basic-N51" data-bid="ILlXsW0AmY">
                                <div className="contents-total">
                                    <strong>총 상품금액</strong>
                                    <p className="contents-price">{totalPrice.toLocaleString()}
                                     <span>원</span></p>
                                </div>
                            </div>
                        </div>
                        {cartItems.length > 0 && (
                            <div className="contents-button">
                                <button className="btnset btnset-round" type="button"
                                        onClick={() => handlePurchase()}>
                                    결제하기</button>
                            </div>
                        )}
                    </div>
                </div>
            </div>

            <CustomModal isOpen={isEditInfoOpen} closeModal={() => {
                setEditInfoOpen(false);
                userRefetch();
            }}>
                <EditInfoModal closeModal={() => {
                    setEditInfoOpen(false);
                    userRefetch();
                }} />
            </CustomModal>

            <CustomModal isOpen={isPurchaseHistoryOpen} closeModal={() => setPurchaseHistoryOpen(false)}>
                <PurchaseHistoryModal closeModal={() => setPurchaseHistoryOpen(false)} />
            </CustomModal>

            <CustomModal isOpen={isCommentsListOpen} closeModal={() => setCommentsListOpen(false)}>
                <CommentsListModal closeModal={() => setCommentsListOpen(false)} />
            </CustomModal>
        </>
    );
};

export default MyPage;
