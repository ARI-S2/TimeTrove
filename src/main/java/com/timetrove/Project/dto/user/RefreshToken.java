package com.timetrove.Project.dto.user;

import lombok.*;

import java.time.LocalDateTime;

// timeToLive: 10일 뒤 레디스에서 데이터 자동제거
// @RedisHash(value = "refreshToken", timeToLive = 864000000)
@Getter
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Builder
public class RefreshToken {
    private String token;
    private Long userCode;
}