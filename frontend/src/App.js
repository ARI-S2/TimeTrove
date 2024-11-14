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
                path="/login/oauth2/callback/kakao" //카카오 소셜 로그인 redirect url
                element={<LoginHandeler/>}
            />
            <Route path="/boards" element={<BoardList/>}/>
            <Route path="/boards/:no" element={<BoardDetail/>}/>
            <Route path="/watches" element={<WatchList/>}/>
            <Route path="/watches/:id" element={<WatchDetail/>}/>
            <Route path="/mypage" element={<MyPage/>}/>
          </Routes>
          <Footer/>
        </Router>
      </CookiesProvider>
    </>
  );
}

export default App;
