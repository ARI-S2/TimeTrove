package com.timetrove.Project.controller;

import com.timetrove.Project.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import com.timetrove.Project.dto.user.UserDto;

import lombok.RequiredArgsConstructor;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class UserController {
	private final UserService userService;

    /**
     * @param userCode 사용자 코드
     * @return 현재 사용자 정보
     */
    @Operation(summary = "현재 사용자 정보 조회", description = "현재 로그인된 사용자의 정보를 조회합니다.")
    @GetMapping("/user")
    public ResponseEntity<Object> getCurrentUser(@AuthenticationPrincipal Long userCode) {
        UserDto userDto = userService.getUserById(userCode);
        return ResponseEntity.ok().body(userDto);
    }

    /**
     * @param userCode 사용자 코드
     * @param profileImage 새로운 프로필 이미지
     * @param kakaoNickname 변경할 닉네임
     * @return 업데이트된 사용자 정보
     */
    @Operation(summary = "사용자 정보 수정", description = "사용자의 프로필 사진과 닉네임을 변경합니다.")
    @PutMapping(value = "/user/update", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<UserDto> updateUserInfo(
            @AuthenticationPrincipal Long userCode,
            @RequestPart(value = "profileImage", required = false) MultipartFile profileImage,
            @RequestPart("kakaoNickname") String kakaoNickname) {
        UserDto updatedUser = userService.updateUserInfo(userCode, profileImage, kakaoNickname);
        return ResponseEntity.ok().body(updatedUser);
    }
}
