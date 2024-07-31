import { useState } from "react";
import { useQuery } from "react-query";
import { Link } from "react-router-dom";
import apiClient from '../../http-commons'
import Pagination from "react-js-pagination";

function WatchList(){
    const [curpage, setCurpage] = useState(1);
    const [searchWord, setSearchWord] = useState("");
    const [filter, setFilter] = useState("no");

    const find=(e)=>{
        setSearchWord(e.target.value)
    }
    const handlePageChange = (page) => {
        setCurpage(page);
    };
    const handleFilterChange = (newFilter) => {
        setFilter(newFilter);
    };
    const { isLoading, isError, error, data } = useQuery(
        ['watch-list', curpage, filter, searchWord],
        async () => {
            const response = await apiClient.get(`/watch/list`, {
                params: {
                    page: curpage,
                    searchWord: searchWord,
                    filter: filter
                }
            });
            return response.data;
        },
        {
            keepPreviousData: true, // 이전 데이터를 유지하면서 새 데이터를 가져옴
        }
    );

    console.log(data);

    if(isLoading) return <h1 className="text-center">서버에서 데이터 전송 지연중...</h1>
    if(isError) return <h1 className="text-center">Error발생:{error}</h1>

    return (
        <div className="basic-N50" data-bid="BfLuawtwI6" id="">
            <div className="contents-inner">
                <div className="contents-container container-md">
                    <div className="textset">
                        <h2 className="textset-tit">전체 상품</h2>
                        <button className="contents-btn btn-filter" type="button">
                            <img src={process.env.PUBLIC_URL + "/icons/ico_filter_black.svg"} alt="모바일 필터 아이콘"/>
                        </button>
                        <div className="inputset inputset-border-bottom">
                            <button className="inputset-icon icon-right icon-search btn" type="button"
                                    aria-label="아이콘"></button>
                            <input type="text" className="inputset-input form-control" placeholder="검색어를 입력하세요"
                                   onChange={find} value={searchWord}/>
                        </div>
                    </div>
                    <div className="contents-body">
                        <div className="contents-left">
                            <div className="contents-filter-header">
                                <strong>필터</strong>
                                <button className="contents-btn btn-close">
                                    <img src={process.env.PUBLIC_URL + "/icons/ico_close_black.svg"} alt="닫기 아이콘"/>
                                </button>
                            </div>
                            <div className="contents-filter-body">
                                <div className="accordset accordset-plus">
                                    <div className="accordset-item">
                                        <div className="accordset-header">
                                            <button className="accordset-button btn" type="button">브랜드</button>
                                        </div>
                                        <div className="accordset-body">
                                            <div className="accordset-content">
                                                <div className="checkset">
                                                    <input id="checkset-1-1" className="checkset-input input-round" type="checkbox" value="" checked=""/>
                                                        <label className="checkset-label" for="checkset-1-1"></label>
                                                        <span className="checkset-text">전체</span>
                                                </div>
                                                <div className="checkset">
                                                    <input id="checkset-1-2" className="checkset-input input-round" type="checkbox" value="" checked=""/>
                                                        <label className="checkset-label" for="checkset-1-2"></label>
                                                        <span className="checkset-text">템하피부</span>
                                                </div>
                                                <div className="checkset">
                                                    <input id="checkset-1-3" className="checkset-input input-round" type="checkbox" value=""/>
                                                        <label className="checkset-label" for="checkset-1-3"></label>
                                                        <span className="checkset-text">템하몰</span>
                                                </div>
                                            </div>
                                        </div>
                                    </div>
                                    <div className="accordset-item">
                                        <div className="accordset-header">
                                            <button className="accordset-button btn" type="button">가격</button>
                                        </div>
                                        <div className="accordset-body">
                                            <div className="accordset-content">
                                                <div className="radioset">
                                                    <input id="radioset-2-1" name="radioset-2" className="radioset-input input-line" type="radio" value=""/>
                                                        <label className="radioset-label" for="radioset-2-1"></label>
                                                        <span className="radioset-text">50,000원 미만</span>
                                                </div>
                                                <div className="radioset">
                                                    <input id="radioset-2-2" name="radioset-2" className="radioset-input input-line" type="radio" value=""/>
                                                        <label className="radioset-label" for="radioset-2-2"></label>
                                                        <span className="radioset-text">50,000원 ~ 100,000원</span>
                                                </div>
                                                <div className="radioset">
                                                    <input id="radioset-2-3" name="radioset-2" className="radioset-input input-line" type="radio" value=""/>
                                                        <label className="radioset-label" for="radioset-2-3"></label>
                                                        <span className="radioset-text">100,000원 이상</span>
                                                </div>
                                            </div>
                                        </div>
                                    </div>
                                    <div className="accordset-item">
                                        <div className="accordset-header">
                                            <button className="accordset-button btn" type="button">피부타입</button>
                                        </div>
                                        <div className="accordset-body">
                                            <div className="accordset-content">
                                                <div className="checkset">
                                                    <input id="checkset-3-1" className="checkset-input input-round" type="checkbox" value="" checked=""/>
                                                        <label className="checkset-label" for="checkset-3-1"></label>
                                                        <span className="checkset-text">전체</span>
                                                </div>
                                                <div className="checkset">
                                                    <input id="checkset-3-2" className="checkset-input input-round" type="checkbox" value=""/>
                                                        <label className="checkset-label" for="checkset-3-2"></label>
                                                        <span className="checkset-text">건성</span>
                                                </div>
                                                <div className="checkset">
                                                    <input id="checkset-3-3" className="checkset-input input-round" type="checkbox" value=""/>
                                                        <label className="checkset-label" for="checkset-3-3"></label>
                                                        <span className="checkset-text">지성</span>
                                                </div>
                                                <div className="checkset">
                                                    <input id="checkset-3-4" className="checkset-input input-round" type="checkbox" value=""/>
                                                        <label className="checkset-label" for="checkset-3-4"></label>
                                                        <span className="checkset-text">민감성</span>
                                                </div>
                                            </div>
                                        </div>
                                    </div>
                                    <div className="accordset-item">
                                        <div className="accordset-header">
                                            <button className="accordset-button btn" type="button">제품라인</button>
                                        </div>
                                        <div className="accordset-body">
                                            <div className="accordset-content">
                                                <div className="checkset">
                                                    <input id="checkset-4-1" className="checkset-input input-round" type="checkbox" value="" checked=""/>
                                                        <label className="checkset-label" for="checkset-4-1"></label>
                                                        <span className="checkset-text">전체</span>
                                                </div>
                                                <div className="checkset">
                                                    <input id="checkset-4-2" className="checkset-input input-round" type="checkbox" value=""/>
                                                        <label className="checkset-label" for="checkset-4-2"></label>
                                                        <span className="checkset-text">마스크 시트</span>
                                                </div>
                                                <div className="checkset">
                                                    <input id="checkset-4-3" className="checkset-input input-round" type="checkbox" value=""/>
                                                        <label className="checkset-label" for="checkset-4-3"></label>
                                                        <span className="checkset-text">크림</span>
                                                </div>
                                            </div>
                                        </div>
                                    </div>
                                    <div className="accordset-item">
                                        <div className="accordset-header">
                                            <button className="accordset-button btn" type="button">사용단계</button>
                                        </div>
                                        <div className="accordset-body">
                                            <div className="accordset-content">
                                                <div className="checkset">
                                                    <input id="checkset-5-1" className="checkset-input input-round" type="checkbox" value="" checked=""/>
                                                        <label className="checkset-label" for="checkset-5-1"></label>
                                                        <span className="checkset-text">전체</span>
                                                </div>
                                                <div className="checkset">
                                                    <input id="checkset-5-2" className="checkset-input input-round" type="checkbox" value=""/>
                                                        <label className="checkset-label" for="checkset-5-2"></label>
                                                        <span className="checkset-text">기초</span>
                                                </div>
                                                <div className="checkset">
                                                    <input id="checkset-5-3" className="checkset-input input-round" type="checkbox" value=""/>
                                                        <label className="checkset-label" for="checkset-5-3"></label>
                                                        <span className="checkset-text">케어</span>
                                                </div>
                                            </div>
                                        </div>
                                    </div>
                                    <div className="accordset-item active">
                                        <div className="accordset-header">
                                            <button className="accordset-button btn" type="button">혜택</button>
                                        </div>
                                        <div className="accordset-body">
                                            <div className="accordset-content">
                                                <div className="checkset">
                                                    <input id="checkset-6-1" className="checkset-input input-round" type="checkbox" value="" checked=""/>
                                                        <label className="checkset-label" for="checkset-6-1"></label>
                                                        <span className="checkset-text">무료배송</span>
                                                </div>
                                                <div className="checkset">
                                                    <input id="checkset-6-2" className="checkset-input input-round" type="checkbox" value=""/>
                                                        <label className="checkset-label" for="checkset-6-2"></label>
                                                        <span className="checkset-text">쿠폰</span>
                                                </div>
                                                <div className="checkset">
                                                    <input id="checkset-6-3" className="checkset-input input-round" type="checkbox" value=""/>
                                                        <label className="checkset-label" for="checkset-6-3"></label>
                                                        <span className="checkset-text">상품할인</span>
                                                </div>
                                                <div className="checkset">
                                                    <input id="checkset-6-4" className="checkset-input input-round" type="checkbox" value=""/>
                                                        <label className="checkset-label" for="checkset-6-4"></label>
                                                        <span className="checkset-text">포인트 적립</span>
                                                </div>
                                                <div className="checkset">
                                                    <input id="checkset-6-5" className="checkset-input input-round" type="checkbox" value=""/>
                                                        <label className="checkset-label" for="checkset-6-5"></label>
                                                        <span className="checkset-text">사은품 증정</span>
                                                </div>
                                            </div>
                                        </div>
                                    </div>
                                </div>
                            </div>
                            <div className="contents-filter-footer">
                                <span>필터초기화</span>
                                <button className="contents-btn btn-refresh">
                                    <img src={process.env.PUBLIC_URL + "/icons/ico_refresh_black.svg"} alt="닫기 아이콘"/>
                                </button>
                            </div>
                        </div>
                        <div className="contents-right">
                            <div className="contents-sort">
                                <p className="contents-sort-total">
                                    총 <span>{data.count}</span>개의 제품이 있습니다.
                                </p>
                                <div className="contents-sort-sel">
                                    <div className="tabset tabset-text">
                                        <ul className="tabset-list">
                                            <li className="tabset-item">
                                                <span
                                                    className="tabset-link active"
                                                    onClick={() => handleFilterChange("no")}
                                                >
                                                    <span>등록순</span>
                                                </span>
                                            </li>
                                            <li className="tabset-item">
                                                <span
                                                    className="tabset-link active"
                                                    onClick={() => handleFilterChange("hit")}
                                                >
                                                    <span>인기순</span>
                                                </span>
                                            </li>
                                            <li className="tabset-item">
                                                <span
                                                    className="tabset-link"
                                                    onClick={() => handleFilterChange("priceHigh")}
                                                >
                                                    <span>높은가격순</span>
                                                </span>
                                            </li>
                                            <li className="tabset-item">
                                                <span
                                                    className="tabset-link"
                                                    onClick={() => handleFilterChange("priceLow")}
                                                >
                                                    <span>낮은가격순</span>
                                                </span>
                                            </li>
                                        </ul>
                                    </div>
                                </div>
                            </div>
                            <div className="contents-list">
                                {data.wList &&
                                    data.wList.map((watch) =>
                                        <Link to={"/watch/detail/" + watch.no} className="cardset cardset-shopping" key={watch.no}>
                                        <figure className="cardset-figure">
                                            <img className="cardset-img" src={watch.image} alt="카드 이미지"/>
                                        </figure>
                                        <div className="cardset-body">
                                            <span className="cardset-name">{watch.model}</span>
                                            <h2 className="cardset-tit">
                                                {watch.name}
                                            </h2>
                                            <p className="cardset-desc">{Number(watch.s_price).toLocaleString()}<small>원</small>
                                            </p>
                                        </div>
                                    </Link>
                                )}
                            </div>
                            <div style={{height: "10px"}}></div>
                            <div className={"row"}>
                                <div className={"text-center"}>
                                    <Pagination
                                        activePage={curpage}
                                        itemsCountPerPage={6}
                                        totalItemsCount={data.count}
                                        pageRangeDisplayed={10}
                                        prevPageText={"<"}
                                        nextPageText={">"}
                                        onChange={handlePageChange}
                                    />
                                </div>
                            </div>
                        </div>
                    </div>
                </div>
            </div>
            <div className="contents-dim"></div>
        </div>
    );
};
    
export default WatchList;
