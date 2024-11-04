package com.timetrove.Project.common.config.auth;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.timetrove.Project.common.enumType.ErrorCode;
import com.timetrove.Project.common.exception.CustomException;
import com.timetrove.Project.domain.User;
import com.timetrove.Project.dto.user.TokenDto;
import com.timetrove.Project.repository.UserRepository;
import com.timetrove.Project.repository.redis.RedisRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.UUID;

@Component
@RequiredArgsConstructor
public class TokenProvider {
    private final RedisRepository redisRepository;
    private final UserRepository userRepository;

    public TokenDto generateTokenPair(User user) {
        String accessToken = createAccessToken(user);
        String refreshToken = createRefreshToken(user.getUserCode());
        return new TokenDto(accessToken, refreshToken);
    }

    public TokenDto reissueTokens(String refreshToken) {
        Long userCode = redisRepository.findHash("refresh-token", refreshToken);
        if (userCode == null) {
            throw new CustomException(HttpStatus.UNAUTHORIZED, ErrorCode.INVALID_REFRESH_TOKEN);
        }

        User user = userRepository.findByUserCode(userCode);
        String newAccessToken = createAccessToken(user);
        String newRefreshToken = createRefreshToken(userCode);

        deleteRefreshToken(refreshToken);

        return new TokenDto(newAccessToken, newRefreshToken);
    }

    private String createAccessToken(User user) {
        return JWT.create()
                .withSubject(String.valueOf(user.getUserCode()))
                .withExpiresAt(new Date(System.currentTimeMillis() + JwtProperties.EXPIRATION_TIME))
                .withClaim("id", user.getUserCode())
                .withClaim("nickname", user.getKakaoNickname())
                .withClaim("roles", user.getUserRole())
                .sign(Algorithm.HMAC512(JwtProperties.SECRET));
    }

    private String createRefreshToken(Long userCode) {
        String token = UUID.randomUUID().toString();
        redisRepository.saveHash("refresh-token", token, userCode, JwtProperties.REFRESH_EXPIRATION_TIME);
        return token;
    }

    public void deleteRefreshToken(String refreshToken) {
        redisRepository.deleteHash("refresh-token", refreshToken);
    }
}