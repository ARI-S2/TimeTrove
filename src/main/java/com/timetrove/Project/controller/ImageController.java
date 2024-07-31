package com.timetrove.Project.controller;

import com.timetrove.Project.service.MypageService;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;


import lombok.RequiredArgsConstructor;

import java.io.IOException;

@RestController
@RequiredArgsConstructor
public class ImageController {
	
	private final MypageService mypageService;

    // 사진이 저장된 경로에서 사진을 바이너리 데이터로 변환 휴 리턴
    @GetMapping("/user/{userCode}/profile-image")
    public ResponseEntity<byte[]> getUserProfileImage(@PathVariable("userCode") Long userCode) {
        try {
            byte[] image = mypageService.getUserProfileImage(userCode);
            return ResponseEntity.ok()
                    .header(HttpHeaders.CONTENT_TYPE, "image/jpeg")
                    .body(image);
        } catch (IOException e) {
            return ResponseEntity.status(500).body(null);
        }
    }
}
