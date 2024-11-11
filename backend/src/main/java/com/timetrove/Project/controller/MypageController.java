package com.timetrove.Project.controller;

import java.util.Map;

import com.timetrove.Project.service.MypageService;
import io.swagger.v3.oas.annotations.Operation;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;


import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/mypage")
public class MypageController {
	
	private final MypageService mypageService;

	/**
	 * @param userCode 사용자 코드
	 * @return 사용자 정보 및 장바구니 목록
	 */
	@Operation(summary = "마이페이지 조회", description = "사용자 정보와 장바구니 목록을 조회합니다.")
	@GetMapping
	public ResponseEntity<Map<String, Object>> getMyPage(@AuthenticationPrincipal Long userCode) {
    	return ResponseEntity.ok().body(mypageService.getMyPage(userCode));
    }

}
