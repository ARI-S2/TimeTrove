import { useEffect, useState } from "react";
import { eraseCookie, getAll, getCookie } from "../util/cookie";
import { Link } from "react-router-dom";

function Footer() {
    const [recentWatches, setRecentWatches] = useState([]);

    useEffect(() => {
        const updateRecentWatches = () => {
            const userId = getCookie('id');
            const cookies = getAll() || {};
            const keys = Object.keys(cookies);
            const values = Object.values(cookies);
            const prefix = userId ? userId + "_watch" : "guest_watch";
            const watchKeys = [];
            const images = [];

            for (let i = keys.length - 1; i >= 0; i--) {
                if (keys[i].startsWith(prefix)) {
                    images.push(values[i]);
                    watchKeys.push(keys[i]);
                }
            }

            // 쿠키가 5개 이상으면 삭제
            if (watchKeys.length > 5) {
                for (let i = 5; i < watchKeys.length; i++) {
                    eraseCookie(watchKeys[i]);
                }
                // 최근 5개만 저장
                watchKeys.splice(5);
                images.splice(5);
            }

            const watches = watchKeys.map((key, index) => ({
                no: key.replace(prefix, ""),
                image: images[index]
            }));

            setRecentWatches(watches);
        };

        updateRecentWatches();

        const intervalId = setInterval(updateRecentWatches, 5000); // 5초마다 업데이트

        return () => clearInterval(intervalId);
    }, []);

    return (
        <footer className="basic-N4" data-bid="UnlUaV0TmD">
            <div className="footer-container container-lg">
                <div className="footer-top">
                    <h2 className="footer-logo">
                        <a href="/" style={{ color: "whitesmoke" }}>
                            TimeTrove
                        </a>
                    </h2>
                    <ul className="footer-menulist">
                        <li className="footer-menuitem">
                            <a href="/terms">
                                <span>이용약관</span>
                            </a>
                        </li>
                        <li className="footer-menuitem">
                            <a href="/privacy-policy">
                                <span>개인정보처리방침</span>
                            </a>
                        </li>
                        <li className="footer-menuitem">
                            <a href="/footer-menu1">
                                <span>제품소개</span>
                            </a>
                        </li>
                        <li className="footer-menuitem">
                            <a href="/footer-menu2">
                                <span>커뮤니티</span>
                            </a>
                        </li>
                    </ul>
                    <ul className="footer-snslist">
                        <li className="footer-snsitem">
                            <a className="footer-snslink" href="https://www.instagram.com/">
                                <img src={process.env.PUBLIC_URL + "/icons/ico_instagram_lightgrey.svg"} alt="인스타그램" />
                            </a>
                        </li>
                        <li className="footer-snsitem">
                            <a className="footer-snslink" href="https://www.youtube.com/">
                                <img src={process.env.PUBLIC_URL + "/icons/ico_youtube_lightgrey.svg"} alt="유튜브" />
                            </a>
                        </li>
                        <li className="footer-snsitem">
                            <a className="footer-snslink" href="https://www.facebook.com/">
                                <img src={process.env.PUBLIC_URL + "/icons/ico_facebook_lightgrey.svg"} alt="페이스북" />
                            </a>
                        </li>
                        <li className="footer-snsitem">
                            <a className="footer-snslink" href="https://www.kakaocorp.com/">
                                <img src={process.env.PUBLIC_URL + "/icons/ico_kakao_lightgrey.svg"} alt="카카오톡" />
                            </a>
                        </li>
                    </ul>
                </div>
                <div className="footer-bottom">
                    <div className="footer-txt">
                        <p>
                            <span>E. duswl966@gmail.com</span>
                        </p>
                    </div>
                    <div className="footer-txt">
                        <p>2024 BY TIMETROVE. ALL RIGHTS RESERVED</p>
                    </div>
                </div>
            </div>

            <div id="cookieBar">
                {recentWatches.length > 0 && (
                    <>
                        <h6>최근 본 목록</h6>
                        <ul>
                            {recentWatches.map((watch) => (
                                <li key={watch.no}>
                                    <Link to={`/watch/detail/${watch.no}`}>
                                        <img src={watch.image} alt={`시계 ${watch.no}`} />
                                    </Link>
                                </li>
                            ))}
                        </ul>
                    </>
                )}
            </div>
        </footer>
    );
}

export default Footer;
