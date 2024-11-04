import { useState } from "react";
import { useQuery } from "react-query";
import { Link } from "react-router-dom";
import apiClient from '../../http-commons';
import Pagination from "react-js-pagination";

function BoardList() {
    const [curpage, setCurpage] = useState(1);
    const [searchWord, setSearchWord] = useState("");

    const { isLoading, isError, error, data, refetch: boardFindData } = useQuery(
        ['board-list', curpage, searchWord],
        async () => {
            const response = await apiClient.get('/boards', {
                params: {
                    page: curpage,
                    searchWord: searchWord
                }
            });
            console.log(response.data);
            return response.data;
        },
        { keepPreviousData: true }
    );

    const handleChange = (page) => {
        setCurpage(page);
    };

    const handleSearchChange = (e) => {
        setSearchWord(e.target.value);
    };

    const handleSearch = () => {
        setCurpage(1);
        boardFindData();
    };

    const handleKeyDown = (e) => {
        if (e.key === "Enter") {
            handleSearch();
        }
    };

    if (isLoading) return <h1 className="text-center">서버에서 데이터 전송 중...</h1>;
    if (isError) return <h1 className="text-center">Error 발생: {error.message}</h1>;

    const boardData = data || { items: [], totalItems: 0, currentPage: 1 };

    return (
        <div className="glamping-N39" data-bid="FXLtPNXh2D">
            <div className="contents-inner">
                <div className="contents-container container-md">
                    <div className="textset">
                        <h2 className="textset-tit">커뮤니티</h2>
                    </div>
                    <Link to="/code/insert/" className="btnset btnset-round" type="button"
                          style={{ marginLeft: "1157px", marginBottom: "10px" }}>
                        글쓰기
                    </Link>
                    <div className="contents-top">
                        <p className="contents-text">
                            총 <strong>{boardData.totalItems}</strong>개의 게시글이 있습니다.
                        </p>
                        <div className="inputset inputset-border-bottom">
                            <button className="inputset-icon icon-right icon-search btn"
                                    type="button"
                                    aria-label="검색"
                                    onClick={handleSearch}>
                            </button>
                            <input
                                type="text"
                                className="inputset-input form-control"
                                placeholder="검색어를 입력하세요"
                                onChange={handleSearchChange}
                                onKeyDown={handleKeyDown} // Trigger search on Enter
                                value={searchWord}
                            />
                        </div>
                    </div>
                    <div className="cardset-wrap">
                        {boardData.items.map((board) => (
                            <Link to={`/boards/${board.no}`}
                                  className="cardset cardset-inner cardset-hover cardset-border"
                                  key={board.no}>
                                <div className="cardset-cont">
                                    <h2 className="cardset-tit">{board.subject}</h2>
                                    <p className="cardset-desc">{board.content}</p>
                                    <span className="cardset-txt">{board.createdAt}</span>
                                </div>
                            </Link>
                        ))}
                    </div>
                </div>

                <div style={{ height: "10px" }}></div>
                <div className="row">
                    <div className="text-center">
                        <Pagination
                            activePage={boardData.currentPage + 1}
                            itemsCountPerPage={6}
                            totalItemsCount={boardData.totalItems}
                            pageRangeDisplayed={10}
                            prevPageText="<"
                            nextPageText=">"
                            onChange={handleChange}
                        />
                    </div>
                </div>
            </div>
        </div>
    );
}

export default BoardList;
