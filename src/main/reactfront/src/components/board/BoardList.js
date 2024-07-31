import { useEffect,useState } from "react";
import { useQuery } from "react-query";
import { Link } from "react-router-dom";
import apiClient from '../../http-commons'
import Pagination from "react-js-pagination";

function BoardList(){
    const [curpage,setCurpage]=useState(1)
    const [searchWord, setSearchWord] = useState("");
    const {isLoading,isError,error,data,refetch:boardFindData}=useQuery(
        ['board-list',curpage],
        async () => {
            const response = await apiClient.get(`/board/list`, {
                params: {
                    page: curpage,
                    searchWord: searchWord
                }
            });
            return response.data;
        }
    )
    console.log(data);
    const handleChange=(page)=>{
        setCurpage(page)
    }
    const find=(e)=>{
        setSearchWord(e.target.value)
    }
    const findBtn=()=>{
        boardFindData()
    }
    if(isLoading) return <h1 className="text-center">서버에서 데이터 전송 지연중...</h1>
    if(isError) return <h1 className="text-center">Error발생:{error}</h1>

    return (
        <>
            <div className="glamping-N39" data-bid="FXLtPNXh2D">
                <div className="contents-inner">
                    <div className="contents-container container-md">
                        <div className="textset">
                            <h2 className="textset-tit">공지사항</h2>
                        </div>
                        <Link to={"/code/insert/"} className="btnset btnset-round" type="buton"
                              style={{marginLeft: "1157px", marginBottom: "10px"}}>글쓰기</Link>
                        <div className="contents-top">
                            <p className="contents-text">총 <strong>{data.count}</strong>개의 공지사항이 있습니다.</p>
                            <div className="inputset inputset-border-bottom">
                                <button className="inputset-icon icon-right icon-search btn" type="button"
                                        aria-label="아이콘" onClick={findBtn}></button>
                                <input type="text" className="inputset-input form-control" placeholder="검색어를 입력하세요"
                                       onChange={find} value={searchWord}/>
                            </div>
                        </div>
                        <div className="cardset-wrap">
                        {data.bList &&
                            data.bList.map((board) =>
                                    <Link to={"/board/detail/" + board.no}
                                          className="cardset cardset-inner cardset-hover cardset-border" key={board.no}>
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
                                onChange={handleChange}
                            />
                        </div>
                    </div>
                </div>
            </div>
        </>
    )
}

export default BoardList