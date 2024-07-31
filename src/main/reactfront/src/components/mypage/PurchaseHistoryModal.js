import React from 'react';
import { useQuery } from 'react-query';
import apiClient from "../../http-commons";

function PurchaseHistoryModal({ closeModal }) {
    const { data: purchaseHistory, error, isLoading } = useQuery('purchaseHistory', async () => {
        const response = await apiClient.get('/mypage/purchases');
        console.log(response.data);
        return response.data;
    });

    if (isLoading) return <div>Loading...</div>;
    if (error) return <div>Error loading data.</div>;

    // 최근 구매 날짜순으로 정렬
    const sortedPurchaseDates = Object.keys(purchaseHistory).sort((a, b) => new Date(b) - new Date(a));

    return (
        <div className="campland-N23" data-bid="UFlYcY40ve" style={{textAlign: "left"}}>
            <button className="btn-close" onClick={closeModal}>닫기</button>
            <div className="contents-group contents-reservation">
                <h2>최근 구매내역</h2>
                <hr/>
                {sortedPurchaseDates.map(purchaseDate => {
                    const totalAmount = purchaseHistory[purchaseDate].reduce((acc, purchase) => acc + purchase.watchPrice * purchase.quantity, 0);
                    return (
                        <div key={purchaseDate} style={{marginTop: '30px'}}>
                            <h3>{new Date(purchaseDate).toLocaleString()}</h3>
                            {purchaseHistory[purchaseDate].map((purchase) => (
                                <div className="cardset cardset-hor cardset-sm" key={purchase.id} style={{
                                    border: '1px solid black',
                                    padding: '10px',
                                    margin: '10px 0',
                                    borderRadius: '20px'
                                }}>
                                    <figure className="cardset-figure"><img className="cardset-img"
                                                                            src={purchase.watchImage}
                                                                            alt="카드 이미지"/></figure>
                                    <div className="cardset-body">
                                        <div className="badgeset-wrap">
                                            <div className="badgeset-group">
                                                <div
                                                    className="badgeset badgeset-fill badgeset-border badgeset-round badgeset-primary">{purchase.watchModel}
                                                </div>
                                            </div>
                                            <p className="cardset-txt"></p>
                                        </div>
                                        <h2 className="cardset-tit">{purchase.watchName}</h2>
                                        <p className="cardset-desc" style={{fontSize: 'larger'}}>
                                            {Number(purchase.watchPrice).toLocaleString()}원 &nbsp; | &nbsp; {purchase.quantity}개
                                        </p>
                                    </div>
                                </div>
                            ))}
                            <div className="basic-N51">
                                <div className="contents-total">
                                    <strong>결제금액</strong>
                                    <p className="contents-price">{totalAmount.toLocaleString()}원</p>
                                </div>
                            </div>
                        </div>
                    );
                })}
            </div>
        </div>
    );
}

export default PurchaseHistoryModal;
