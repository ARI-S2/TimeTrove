const CLIENT_ID = "d008d7a526a3e250c2bd63146f16c578";
const REDIRECT_URI = `http://${process.env.REACT_APP_EC2_HOST_URL}:3000/login/oauth2/callback/kakao`;

export const KAKAO_AUTH_URL = `https://kauth.kakao.com/oauth/authorize?client_id=${CLIENT_ID}&redirect_uri=${REDIRECT_URI}&response_type=code`;
