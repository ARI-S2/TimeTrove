import { BrowserRouter as Router,Routes,Route } from "react-router-dom";
import { CookiesProvider } from "react-cookie";
import Header from "./components/main/Header";
import Footer from "./components/main/Footer";
import Home from "./components/main/Home";
import LoginHandeler from "./components/member/LoginHandler";
import BoardList from "./components/board/BoardList";
import BoardDetail from "./components/board/BoardDetail";
import WatchList from "./components/watch/WatchList";
import WatchDetail from "./components/watch/WatchDetail";
import MyPage from "./components/mypage/MyPage";

function App() {
  return (
    <>
      <CookiesProvider>
        <Router>
          <Header/>
          <Routes>
            <Route path="/" element={<Home/>}/>
            <Route
                path="/login/oauth2/callback/kakao" //redirect_url
                element={<LoginHandeler/>} //당신이 redirect_url에 맞춰 꾸밀 컴포넌트
            />
            <Route path="/board/list" element={<BoardList/>}/>
            <Route path="/board/detail/:no" element={<BoardDetail/>}/>
            <Route path="/watch/list" element={<WatchList/>}/>
            <Route path="/watch/detail/:no" element={<WatchDetail/>}/>
            <Route path="/mypage" element={<MyPage/>}/>
          </Routes>
          <Footer/>
        </Router>
      </CookiesProvider>
    </>
  );
}

export default App;
