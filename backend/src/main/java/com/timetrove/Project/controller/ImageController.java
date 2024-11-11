package com.timetrove.Project.controller;

import com.timetrove.Project.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;


import lombok.RequiredArgsConstructor;

import java.io.IOException;

@RestController
@RequiredArgsConstructor
public class ImageController {
	
	private final UserService userService;

    /**
     * @param userCode 사용자 코드
     * @return 사용자 프로필 이미지
     */
    @Operation(summary = "사용자 프로필 이미지 조회", description = "사진이 저장된 경로에서 사진을 바이너리 데이터로 변환 후 리턴합니다.")
    @GetMapping("/api/user/{userCode}/profile-image")
    public ResponseEntity<byte[]> getUserProfileImage(@PathVariable("userCode") Long userCode) throws IOException {
            byte[] imageData = userService.getUserProfileImage(userCode);
            return ResponseEntity.ok()
                    .contentType(MediaType.IMAGE_JPEG)
                    .body(imageData);
    }
}
