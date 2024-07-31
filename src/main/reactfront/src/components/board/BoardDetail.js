import React, {useEffect, useState} from 'react';
import { useQuery } from "react-query";
import apiClient from '../../http-commons';
import { useParams } from "react-router-dom";
import { useUserData } from "../member/authHooks";
import CommentContainer from './CommentContainer';
import {fetchProfileImage} from "../util/api";

function BoardDetail() {
    const { no } = useParams();
    const [profileImageUrl, setProfileImageUrl] = useState(null);

    const { isLoading: isUserLoading, isError: isUserError, data: userData } = useUserData();

    const { isLoading, isError, error, data: boardData, refetch: boardDetail } = useQuery(['board-detail', no],
        async () => {
            const response = await apiClient.get(`/board/detail/${no}`);
            return response.data;
        }
    );

    useEffect(() => {
        if (userData) {
            if (userData.kakaoProfileImg.startsWith('http://k.kakaocdn.net/')) {
                setProfileImageUrl(userData.kakaoProfileImg);
            } else {
                fetchProfileImage(userData.userCode).then(setProfileImageUrl);
            }
        }
    }, [userData])

    useEffect(() => {
        boardDetail();
    }, [no]);

    if (isLoading || isUserLoading) return <h1 className="text-center">서버에서 전송 지연중...</h1>;
    if (isError) return <h1 className="text-center">Error발생: {error}</h1>;
    if (isUserError) return <h1 className="text-center">Error발생: 사용자 정보를 가져오는 중 오류 발생</h1>;

    return (
        <div className="campland-N13" data-bid="vnLT3364X9">
            <div className="contents-inner">
                <div className="contents-container container-md">
                    <div className="textset textset-sub">
                        <h2 className="textset-tit">{boardData.subject}</h2>
                        <p className="textset-desc">
                            관리자 <span>{boardData.regdate}</span>
                        </p>
                    </div>
                    <div className="contents-body">
                        <p className="contents-text">{boardData.content}</p>
                    </div>
                    <div className="contents-link">
                        <a href="../notice/list.do" className="btnset btnset-round btnset-line btnset-black">
                            목록으로
                        </a>
                    </div>
                </div>
            </div>
            {userData && (
                <CommentContainer
                    boardNo={no}
                    userId={userData.userCode}
                    profile={profileImageUrl}
                    nickname={userData.kakaoNickname}
                />
            )}
        </div>
    );
}

export default BoardDetail;
