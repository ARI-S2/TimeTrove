package com.timetrove.Project.service;


import com.timetrove.Project.common.config.auth.TokenProvider;
import com.timetrove.Project.domain.User;
import com.timetrove.Project.dto.user.KakaoProfile;
import com.timetrove.Project.dto.user.TokenDto;
import com.timetrove.Project.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Slf4j
public class AuthService {
    private final KakaoOAuthService kakaoOAuthService;
    private final UserRepository userRepository;
    private final TokenProvider tokenProvider;

    /**
     * 사용자 인증 메서드
     * 카카오 코드로 사용자 인증 및 토큰 생성
     * @param: String code - 카카오 인증 코드
     * @return 생성된 TokenDto
     */
    @Transactional
    public TokenDto authenticateUser(String code) {
        String kakaoAccessToken = kakaoOAuthService.getAccessToken(code);
        KakaoProfile profile = kakaoOAuthService.findProfile(kakaoAccessToken);

        User user = userRepository.findById(profile.getId())
                .orElseGet(() -> createUser(profile));

        return tokenProvider.generateTokenPair(user);
    }

    /**
     * 새 사용자 생성 메서드
     * 카카오 프로필 정보를 바탕으로 사용자 생성
     * @param: KakaoProfile profile - 카카오 프로필 정보
     * @return User
     */
    private User createUser(KakaoProfile profile) {
        User user = User.builder()
                .userCode(profile.getId())
                .kakaoProfileImg(profile.getKakao_account().getProfile().getProfile_image_url())
                .kakaoNickname(profile.getKakao_account().getProfile().getNickname())
                .userRole("ROLE_USER")
                .build();

        return userRepository.save(user);
    }

    /**
     * 토큰 재발급 메서드
     * 만료된 리프레시 토큰으로 새로운 액세스 및 리프레시 토큰 발급
     * @param: String refreshToken - 리프레시 토큰
     * @return 재발급된 TokenDto
     */
    @Transactional
    public TokenDto reissueTokens(String refreshToken) {
        return tokenProvider.reissueTokens(refreshToken);
    }

    /**
     * 로그아웃 메서드
     * 리프레시 토큰을 삭제하여 로그아웃 처리
     * @param: String refreshToken - 리프레시 토큰
     */
    public void logout(String refreshToken) {
        tokenProvider.deleteRefreshToken(refreshToken);
    }
}

