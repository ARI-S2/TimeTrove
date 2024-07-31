import React, { useState, useEffect } from "react";
import { Link, useNavigate } from "react-router-dom";
import {fetchProfileImage } from '../util/api';
import {useUserData} from "../member/authHooks";
import { logout } from "../util/cookie";
import { KAKAO_AUTH_URL } from "../member/OAuth";

function Header() {
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

    const handleLogout = () => {
        logout();
        refetch();
        window.location.reload();
    };

    if (isLoading) return <div>Loading...</div>;

    return (
        <header className="basic-N1" data-bid="TpLUAv0Rob">
            <div className="header-inner">
                <div className="header-container container-lg">
                    <h1 className="header-title">
                        <a href="/">
                            <img src={process.env.PUBLIC_URL + "/images/img_logo_black.png"} alt="로고"/>
                        </a>
                    </h1>
                    <div className="header-center">
                        <div className="header-title header-mobile-top">
                            <a href="/">
                                <img src={process.env.PUBLIC_URL + "/images/img_logo_black.png"} alt="로고"/>
                            </a>
                        </div>
                        <ul className="header-gnblist">
                            <li className="header-gnbitem">
                                <a className="header-gnblink" href="/company">
                                    <span>회사소개</span>
                                </a>
                            </li>
                            <li className="header-gnbitem">
                                <a className="header-gnblink" href="/business">
                                    <span>사업소개</span>
                                </a>
                            </li>
                            <li className="header-gnbitem">
                                <a className="header-gnblink" href="/products">
                                    <span>제품소개</span>
                                </a>
                            </li>
                            <li className="header-gnbitem">
                                <a className="header-gnblink" href="/press">
                                    <span>홍보센터</span>
                                </a>
                            </li>
                            <li className="header-gnbitem">
                                <a className="header-gnblink" href="/customer-service">
                                    <span>고객센터</span>
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
                            {/*<button className="header-langbtn">KOR</button>
                            <ul className="header-langlist">
                                <li className="header-langitem">
                                    <a href="/">KOR</a>
                                </li>
                                <li className="header-langitem">
                                    <a href="/">ENG</a>
                                </li>
                            </ul>*/}
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
