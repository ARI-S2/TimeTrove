import {useQuery} from "react-query";
import apiClient from '../../http-commons'
import {Link} from "react-router-dom";
import SwiperCore, { Navigation, Pagination, Autoplay } from 'swiper';
import 'swiper/swiper-bundle.min.css';
import { useEffect } from 'react';
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
                                                    <h2 className="textset-tit" style={{color : "white"}}>Temha Portfolio</h2>
                                                    <p className="textset-desc">지속적인 학습과 개발을 통해 최신 기술과 트렌드를 적용하여 혁신적인
                                                        결과물을 제시했습니다.</p>
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
                                <h2 className="textset-tit">Best Product</h2>
                                <p className="textset-desc">베스트 상품을 지금 바로 만나보세요!</p>
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
                                    {data.wList && data.wList.map((watch) => (
                                        <SwiperSlide key={watch.no}>
                                            <div className="swiper-slide" data-swiper-slide-index="0">
                                                <Link to={"/watch/detail/" + watch.no}
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
                                                        <p className="cardset-desc">{Number(watch.s_price).toLocaleString()}<small>원</small>
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
                {/*
                <div className="basic-N42" data-bid="HHlUaV8w2c">
                    <div className="contents-inner">
                        <div className="contents-container container-md">
                            <div className="textset">
                                <h2 className="textset-tit">Best Product</h2>
                                <p className="textset-desc">베스트 상품을 지금 바로 만나보세요!</p>
                            </div>
                            <div
                                className="swiper contents-swiper swiper-initialized swiper-horizontal swiper-pointer-events">
                                <div className="swiper-wrapper" id="swiper-wrapper-a2c1438ecb8fa257" aria-live="off"
                                     style={{transform: "translate3d(-2352px, 0px, 0px)", transitionDuration: "0ms"}}>
                                    <div className="swiper-slide swiper-slide-duplicate" data-swiper-slide-index="0"
                                         role="group" aria-label="1 / 4">
                                        <a href="javascript:void(0);" className="cardset cardset-shopping">
                                            <figure className="cardset-figure">
                                                <img className="cardset-img"
                                                     src={process.env.PUBLIC_URL + "/images/img_basic_N42_1.png"}
                                                     alt="스와이퍼 이미지"/>
                                            </figure>
                                            <div className="cardset-body">
                                                <span className="cardset-name">Temhamol</span>
                                                <h2 className="cardset-tit"> 템하몰 방문손잡이 (3color) </h2>
                                                <p className="cardset-desc">32,000<small>원</small>
                                                </p>
                                            </div>
                                        </a>
                                    </div>
                                    <div className="swiper-slide swiper-slide-duplicate" data-swiper-slide-index="1"
                                         role="group" aria-label="2 / 4">
                                        <a href="javascript:void(0);" className="cardset cardset-shopping">
                                            <figure className="cardset-figure">
                                                <img className="cardset-img"
                                                     src={process.env.PUBLIC_URL + "/images/img_basic_N42_2.png"}
                                                     alt="스와이퍼 이미지"/>
                                            </figure>
                                            <div className="cardset-body">
                                                <span className="cardset-name">Temhamol</span>
                                                <h2 className="cardset-tit"> 템하몰 방문손잡이 (3color) </h2>
                                                <p className="cardset-desc">32,000<small>원</small>
                                                </p>
                                            </div>
                                        </a>
                                    </div>
                                    <div className="swiper-slide swiper-slide-duplicate" data-swiper-slide-index="2"
                                         role="group" aria-label="3 / 4">
                                        <a href="javascript:void(0);" className="cardset cardset-shopping">
                                            <figure className="cardset-figure">
                                                <img className="cardset-img"
                                                     src={process.env.PUBLIC_URL + "/images/img_basic_N42_3.png"}
                                                     alt="스와이퍼 이미지"/>
                                            </figure>
                                            <div className="cardset-body">
                                                <span className="cardset-name">Temhamol</span>
                                                <h2 className="cardset-tit"> 템하몰 방문손잡이 (3color) </h2>
                                                <p className="cardset-desc">32,000<small>원</small>
                                                </p>
                                            </div>
                                        </a>
                                    </div>
                                    <div className="swiper-slide swiper-slide-duplicate swiper-slide-duplicate-prev"
                                         data-swiper-slide-index="3" role="group" aria-label="4 / 4">
                                        <a href="javascript:void(0);" className="cardset cardset-shopping">
                                            <figure className="cardset-figure">
                                                <img className="cardset-img"
                                                     src={process.env.PUBLIC_URL + "/images/img_basic_N42_4.png"}
                                                     alt="스와이퍼 이미지"/>
                                            </figure>
                                            <div className="cardset-body">
                                                <span className="cardset-name">Temhamol</span>
                                                <h2 className="cardset-tit"> 템하몰 방문손잡이 (3color) </h2>
                                                <p className="cardset-desc">32,000<small>원</small>
                                                </p>
                                            </div>
                                        </a>
                                    </div>
                                    <div className="swiper-slide swiper-slide-duplicate-active"
                                         data-swiper-slide-index="0" role="group" aria-label="1 / 4">
                                        <a href="javascript:void(0);" className="cardset cardset-shopping">
                                            <figure className="cardset-figure">
                                                <img className="cardset-img"
                                                     src={process.env.PUBLIC_URL + "/images/img_basic_N42_1.png"}
                                                     alt="스와이퍼 이미지"/>
                                            </figure>
                                            <div className="cardset-body">
                                                <span className="cardset-name">Temhamol</span>
                                                <h2 className="cardset-tit"> 템하몰 방문손잡이 (3color) </h2>
                                                <p className="cardset-desc">32,000<small>원</small>
                                                </p>
                                            </div>
                                        </a>
                                    </div>
                                    <div className="swiper-slide swiper-slide-duplicate-next"
                                         data-swiper-slide-index="1" role="group" aria-label="2 / 4">
                                        <a href="javascript:void(0);" className="cardset cardset-shopping">
                                            <figure className="cardset-figure">
                                                <img className="cardset-img"
                                                     src={process.env.PUBLIC_URL + "/images/img_basic_N42_2.png"}
                                                     alt="스와이퍼 이미지"/>
                                            </figure>
                                            <div className="cardset-body">
                                                <span className="cardset-name">Temhamol</span>
                                                <h2 className="cardset-tit"> 템하몰 방문손잡이 (3color) </h2>
                                                <p className="cardset-desc">32,000<small>원</small>
                                                </p>
                                            </div>
                                        </a>
                                    </div>
                                    <div className="swiper-slide" data-swiper-slide-index="2" role="group"
                                         aria-label="3 / 4">
                                        <a href="javascript:void(0);" className="cardset cardset-shopping">
                                            <figure className="cardset-figure">
                                                <img className="cardset-img"
                                                     src={process.env.PUBLIC_URL + "/images/img_basic_N42_3.png"}
                                                     alt="스와이퍼 이미지"/>
                                            </figure>
                                            <div className="cardset-body">
                                                <span className="cardset-name">Temhamol</span>
                                                <h2 className="cardset-tit"> 템하몰 방문손잡이 (3color) </h2>
                                                <p className="cardset-desc">32,000<small>원</small>
                                                </p>
                                            </div>
                                        </a>
                                    </div>
                                    <div className="swiper-slide swiper-slide-prev" data-swiper-slide-index="3"
                                         role="group" aria-label="4 / 4">
                                        <a href="javascript:void(0);" className="cardset cardset-shopping">
                                            <figure className="cardset-figure">
                                                <img className="cardset-img"
                                                     src={process.env.PUBLIC_URL + "/images/img_basic_N42_4.png"}
                                                     alt="스와이퍼 이미지"/>
                                            </figure>
                                            <div className="cardset-body">
                                                <span className="cardset-name">Temhamol</span>
                                                <h2 className="cardset-tit"> 템하몰 방문손잡이 (3color) </h2>
                                                <p className="cardset-desc">32,000<small>원</small>
                                                </p>
                                            </div>
                                        </a>
                                    </div>
                                    <div className="swiper-slide swiper-slide-duplicate swiper-slide-active"
                                         data-swiper-slide-index="0" role="group" aria-label="1 / 4">
                                        <a href="javascript:void(0);" className="cardset cardset-shopping">
                                            <figure className="cardset-figure">
                                                <img className="cardset-img"
                                                     src={process.env.PUBLIC_URL + "/images/img_basic_N42_1.png"}
                                                     alt="스와이퍼 이미지"/>
                                            </figure>
                                            <div className="cardset-body">
                                                <span className="cardset-name">Temhamol</span>
                                                <h2 className="cardset-tit"> 템하몰 방문손잡이 (3color) </h2>
                                                <p className="cardset-desc">32,000<small>원</small>
                                                </p>
                                            </div>
                                        </a>
                                    </div>
                                    <div className="swiper-slide swiper-slide-duplicate swiper-slide-next"
                                         data-swiper-slide-index="1" role="group" aria-label="2 / 4">
                                        <a href="javascript:void(0);" className="cardset cardset-shopping">
                                            <figure className="cardset-figure">
                                                <img className="cardset-img"
                                                     src={process.env.PUBLIC_URL + "/images/img_basic_N42_2.png"}
                                                     alt="스와이퍼 이미지"/>
                                            </figure>
                                            <div className="cardset-body">
                                                <span className="cardset-name">Temhamol</span>
                                                <h2 className="cardset-tit"> 템하몰 방문손잡이 (3color) </h2>
                                                <p className="cardset-desc">32,000<small>원</small>
                                                </p>
                                            </div>
                                        </a>
                                    </div>
                                    <div className="swiper-slide swiper-slide-duplicate" data-swiper-slide-index="2"
                                         role="group" aria-label="3 / 4">
                                        <a href="javascript:void(0);" className="cardset cardset-shopping">
                                            <figure className="cardset-figure">
                                                <img className="cardset-img"
                                                     src={process.env.PUBLIC_URL + "/images/img_basic_N42_3.png"}
                                                     alt="스와이퍼 이미지"/>
                                            </figure>
                                            <div className="cardset-body">
                                                <span className="cardset-name">Temhamol</span>
                                                <h2 className="cardset-tit"> 템하몰 방문손잡이 (3color) </h2>
                                                <p className="cardset-desc">32,000<small>원</small>
                                                </p>
                                            </div>
                                        </a>
                                    </div>
                                    <div className="swiper-slide swiper-slide-duplicate swiper-slide-duplicate-prev"
                                         data-swiper-slide-index="3" role="group" aria-label="4 / 4">
                                        <a href="javascript:void(0);" className="cardset cardset-shopping">
                                            <figure className="cardset-figure">
                                                <img className="cardset-img"
                                                     src={process.env.PUBLIC_URL + "/images/img_basic_N42_4.png"}
                                                     alt="스와이퍼 이미지"/>
                                            </figure>
                                            <div className="cardset-body">
                                                <span className="cardset-name">Temhamol</span>
                                                <h2 className="cardset-tit"> 템하몰 방문손잡이 (3color) </h2>
                                                <p className="cardset-desc">32,000<small>원</small>
                                                </p>
                                            </div>
                                        </a>
                                    </div>
                                </div>
                                <span className="swiper-notification" aria-live="assertive" aria-atomic="true"></span>
                            </div>
                            <div className="swiper-button-prev" tabindex="0" role="button" aria-label="Previous slide"
                                 aria-controls="swiper-wrapper-a2c1438ecb8fa257"></div>
                            <div className="swiper-button-next" tabindex="0" role="button" aria-label="Next slide"
                                 aria-controls="swiper-wrapper-a2c1438ecb8fa257"></div>
                            <div
                                className="swiper-pagination swiper-pagination-progressbar swiper-pagination-horizontal">
                                <span className="swiper-pagination-progressbar-fill" style={{
                                    transform: "translate3d(0px, 0px, 0px) scaleX(0.25) scaleY(1)",
                                    transitionDuration: "300ms"
                                }}></span></div>
                        </div>
                    </div>
                </div>*/}
            </main>

            <div className="basic-N9" data-bid="xMLUAv9Nod" id="">
                <div className="contents-inner">
                    <div className="contents-container">
                        <div className="contents-bottom container-md">
                            <div className="textset">
                                <h2 className="textset-tit">Notice &amp; News</h2>
                                <p className="textset-desc">
                                    웹사이트는 비즈니스의 온라인 존재감을 나타내는 중요한 수단이며, 비즈니스가 웹사이트를 보유하면 전 세계 어디에서든 제품과 서비스에 대한 정보를 제공할
                                    수 있습니다. 이를 통해 새로운 고객을 유치하고, 기존 고객과의 관계를 유지할 수 있습니다.
                                </p>
                            </div>
                            <div className="contents-cardset">
                                <div className="cardset-wrap">
                                    {data.bList &&
                                        data.bList.map((board) =>
                                            <Link to={"/board/detail/" + board.no}
                                                  className="cardset cardset-inner cardset-hover cardset-border"
                                                  key={board.no}>
                                                <div className="cardset-cont">
                                                    <h2 className="cardset-tit">{board.subject}</h2>
                                                    <p className="cardset-desc">
                                                        {board.content}
                                                    </p>
                                                    <span className="cardset-txt">{board.regdate}</span>
                                                </div>
                                            </Link>
                                        )}
                                </div>
                            </div>
                            <div className="contents-btn">
                                <a href="javascript:void(0);" className="btnset btnset-icon icon-left icon-more">더보기</a>
                            </div>
                        </div>
                    </div>
                </div>
            </div>
        </>
    );
}

export default Home