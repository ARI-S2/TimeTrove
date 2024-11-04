import {useQuery} from "react-query";
import apiClient from '../../http-commons'
import {Link} from "react-router-dom";
import SwiperCore, { Navigation, Pagination, Autoplay } from 'swiper';
import 'swiper/swiper-bundle.min.css';
import { Swiper, SwiperSlide } from 'swiper/react';
import 'swiper/swiper.min.css';

function Home(){
    const { isLoading, isError, error, data } = useQuery(
        'main-data',
        async () => {
            const response = await apiClient.get('/');
            return response.data;
        }
    );
    SwiperCore.use([Navigation, Pagination, Autoplay]);

    if (isLoading) return <h1 className="text-center">Loading</h1>;
    if (isError) return <h1 className="text-center">{error.message}</h1>;
    console.log(data);

    return (
        <>
            <main className="th-layout-main ">
                <div className="basic-N25" data-bid="UjLuAV6yMu">
                    <div className="contents-inner">
                        <div className="contents-container ">
                            <div
                                className="contents-swiper swiper-initialized swiper-horizontal swiper-pointer-events swiper-backface-hidden">
                                <div className="swiper-wrapper" id="swiper-wrapper-73e46a5613d7a107a" aria-live="off"
                                     >
                                    <div className="swiper-slide swiper-slide-active" role="group" aria-label="1 / 4"
                                         >
                                        <img className="contents-backimg img-pc"
                                             src={"https://the1916company.imgix.net/cms/2488x900_NO_TEXT_N_A_FF_DESKTOP_GMT_Master_II_M126710grnr_0003_STATIC_JPEG_84d7fda950.jpg"}
                                             alt="PC 배너 이미지"/>
                                        <img className="contents-backimg img-mobile"
                                             src={"https://the1916company.imgix.net/cms/2488x900_NO_TEXT_N_A_FF_DESKTOP_GMT_Master_II_M126710grnr_0003_STATIC_JPEG_84d7fda950.jpg"}
                                             alt="모바일 배너 이미지"/>
                                        <a className="content-link container-md" href="javascript:void(0);">
                                            <div className="contents-slide-group">
                                                <div className="textset textset-visual">
                                                    <h2 className="textset-tit" style={{color : "white"}}>Time Trove</h2>
                                                    <p className="textset-desc">
                                                        고급스러운 디자인부터 실용적인 모델까지, 당신의 취향에 맞는 완벽한 시계를 찾아 당신의 시간에 특별함을 더하세요.
                                                    </p>
                                                </div>
                                            </div>
                                        </a>
                                    </div>
                                </div>
                                <span className="swiper-notification" aria-live="assertive" aria-atomic="true"></span>
                            </div>
                            <div className="contents-control ">
                                <div className="contents-control-top">
                                    <div className="swiper-scrollbar swiper-scrollbar-horizontal">
                                        <div className="swiper-scrollbar-drag" style={{
                                            transform: "translate3d(0px, 0px, 0px)",
                                            width: "172.5px",
                                            transitionDuration: "0ms"
                                        }}></div>
                                    </div>
                                    <div
                                        className="swiper-pagination swiper-pagination-clickable swiper-pagination-bullets swiper-pagination-horizontal">
                                        <span className="swiper-pagination-bullet swiper-pagination-bullet-active"
                                              tabindex="0" aria-current="true">PROJECT 01</span><span
                                        className="swiper-pagination-bullet" tabindex="0">PROJECT 02</span><span
                                        className="swiper-pagination-bullet" tabindex="0">PROJECT 03</span><span
                                        className="swiper-pagination-bullet" tabindex="0">PROJECT 04</span></div>
                                </div>
                            </div>
                        </div>
                    </div>
                </div>
                <div className="basic-N42" data-bid="HHlUaV8w2c">
                    <div className="contents-inner">
                        <div className="contents-container container-md">
                            <div className="textset">
                                <h2 className="textset-tit">실시간 인기상품</h2>
                                <p className="textset-desc">현재 고객님들께 가장 사랑받고 있는 인기상품을 소개합니다. 최신 트렌드와 뛰어난 기능성을 갖춘 시계를 만나보세요!</p>
                            </div>
                            <div className="swiper contents-swiper">
                                <Swiper
                                    spaceBetween={30}
                                    slidesPerView={4}
                                    navigation={{
                                        nextEl: '.swiper-button-next',
                                        prevEl: '.swiper-button-prev',
                                    }}
                                    pagination={{clickable: true}}
                                    autoplay={{
                                        delay: 3000,
                                        disableOnInteraction: false,
                                    }}
                                    onSwiper={(swiper) => console.log(swiper)}
                                    onSlideChange={() => console.log('slide change')}
                                >
                                    {data.topWatchList && data.topWatchList.map((watch) => (
                                        <SwiperSlide key={watch.id}>
                                            <div className="swiper-slide" data-swiper-slide-index="0">
                                                <Link to={"/watches/" + watch.id}
                                                      className="cardset cardset-shopping">
                                                    <figure className="cardset-figure">
                                                        <img
                                                            className="cardset-img"
                                                            src={watch.image}
                                                            alt="스와이퍼 이미지"
                                                        />
                                                    </figure>
                                                    <div className="cardset-body">
                                                        <span className="cardset-name">{watch.model}</span>
                                                        <h2 className="cardset-tit">{watch.name}</h2>
                                                        <p className="cardset-desc">{Number(watch.soldPrice).toLocaleString()}<small>원</small>
                                                        </p>
                                                    </div>
                                                </Link>
                                            </div>
                                        </SwiperSlide>
                                    ))}
                                </Swiper>
                                <span className="swiper-notification" aria-live="assertive" aria-atomic="true"></span>
                                <div
                                    className="swiper-button-prev"
                                    tabIndex="0"
                                    role="button"
                                    aria-label="Previous slide"
                                    aria-controls="swiper-wrapper-a2c1438ecb8fa257"
                                ></div>
                                <div
                                    className="swiper-button-next"
                                    tabIndex="0"
                                    role="button"
                                    aria-label="Next slide"
                                    aria-controls="swiper-wrapper-a2c1438ecb8fa257"
                                ></div>
                                <div
                                    className="swiper-pagination swiper-pagination-clickable swiper-pagination-bullets swiper-pagination-horizontal"
                                ></div>
                            </div>
                        </div>
                    </div>
                </div>
            </main>

            <div className="basic-N9" data-bid="xMLUAv9Nod" id="">
                <div className="contents-inner">
                    <div className="contents-container">
                        <div className="contents-bottom container-md">
                            <div className="textset">
                                <h2 className="textset-tit">실시간 인기글</h2>
                                <p className="textset-desc">
                                    시계에 관한 흥미로운 이야기와 유용한 정보를 담은 인기글을 확인하세요. 최신 트렌드와 팁을 통해 현명한 쇼핑을 도와드립니다!
                                </p>
                            </div>
                            <div className="contents-cardset">
                                <div className="cardset-wrap">
                                    {data.topBoardList &&
                                        data.topBoardList.map((board) =>
                                            <Link to={"/boards/" + board.no}
                                                  className="cardset cardset-inner cardset-hover cardset-border"
                                                  key={board.no}>
                                                <div className="cardset-cont">
                                                    <h2 className="cardset-tit">{board.subject}</h2>
                                                    <p className="cardset-desc">
                                                        {board.content}
                                                    </p>
                                                    <span className="cardset-txt">{board.createAt}</span>
                                                </div>
                                            </Link>
                                        )}
                                </div>
                            </div>
                            <div className="contents-btn">
                                <a href="/boards" className="btnset btnset-icon icon-left icon-more">더보기</a>
                            </div>
                        </div>
                    </div>
                </div>
            </div>
        </>
    );
}

export default Home