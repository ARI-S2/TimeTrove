import React, { useState, useEffect } from "react";
import { Link, useNavigate } from "react-router-dom";
import {fetchProfileImage } from '../util/api';
import {useUserData} from "../member/authHooks";
import {getCookie, logout} from "../util/cookie";
import { KAKAO_AUTH_URL } from "../member/OAuth";
import apiClient from "../../http-commons";

function Header() {
    const navigate = useNavigate();
    const [profileImageUrl, setProfileImageUrl] = useState(null);

    const { data: userData, error, isLoading, refetch } = useUserData();

    useEffect(() => {
        if (userData) {
            if (userData.kakaoProfileImg.startsWith('http://k.kakaocdn.net/')) {
                setProfileImageUrl(userData.kakaoProfileImg);
            } else {
                fetchProfileImage(userData.userCode).then(setProfileImageUrl);
            }
        }
    }, [userData])

    const handleLogout = async () => {
        try {
            //await apiClient.post(`/logout`);
            logout();
            refetch();
            navigate("/");
            window.location.reload();
        } catch (error) {
            console.error('로그아웃 실패:', error);
        }
    };

    if (isLoading) return <div>Loading...</div>;

    return (
        <header className="basic-N1" data-bid="TpLUAv0Rob">
            <div className="header-inner">
                <div className="header-container container-lg">
                    <h2 className="header-title">
                        <a href="/">
                            TimeTrove
                        </a>
                    </h2>
                    <div className="header-center">
                        <div className="header-title header-mobile-top">
                            <a href="/">
                                TimeTrove
                            </a>
                        </div>
                        <ul className="header-gnblist">
                            <li className="header-gnbitem">
                                <a className="header-gnblink" href="/watches">
                                    <span>제품소개</span>
                                </a>
                            </li>
                            <li className="header-gnbitem">
                                <a className="header-gnblink" href="/boards">
                                    <span>커뮤니티</span>
                                </a>
                            </li>
                        </ul>
                    </div>
                    <div className="header-right">
                        <div className="header-lang">
                            {userData ? (
                                <div className="login" style={{display: "flex", alignItems: "center"}}>
                                    <Link to="/mypage">
                                        <img
                                            src={profileImageUrl}
                                            style={{width: "40px", height: "40px", borderRadius: "50%"}}
                                            alt="프로필"
                                        />
                                    </Link>
                                    <img
                                        onClick={handleLogout}
                                        src={process.env.PUBLIC_URL + `/icons/logout.png`}
                                        style={{width: "30px", marginTop: "20px", marginLeft: "10px", marginBottom: "18px"}}
                                        alt="로그아웃"
                                    />
                                </div>
                            ) : (
                                <Link to={KAKAO_AUTH_URL} className="kakaobtn">
                                    <img src={process.env.PUBLIC_URL + `/icons/kakao_login.png`} alt={"kakaoLogin"}/>
                                </Link>
                            )}
                        </div>
                        <div className="header-utils">
                            <button className="btn-allmenu">
                                <img src={process.env.PUBLIC_URL + "/icons/ico_menu_black.svg"} alt="전체 메뉴 아이콘"/>
                            </button>
                            <button className="btn-momenu">
                                <img src={process.env.PUBLIC_URL + "/icons/ico_menu_black.svg"} alt="모바일 메뉴 아이콘"/>
                            </button>
                            <button className="btn-close">
                                <img src={process.env.PUBLIC_URL + "/icons/ico_close_black.svg"} alt="닫기 아이콘"/>
                            </button>
                        </div>
                    </div>
                </div>
            </div>
            <div className="header-dim"></div>
        </header>
    );
}

export default Header;
