package com.timetrove.Project.controller;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

import com.timetrove.Project.dto.CartDto;
import com.timetrove.Project.dto.CommentDto;
import com.timetrove.Project.service.MypageService;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;


import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/mypage")
public class MypageController {
	
	private final MypageService mypageService;
	
	// 사용자 정보, 장바구니 정보 조회
	@GetMapping
	public ResponseEntity<Map<String, Object>> getMyPage(@AuthenticationPrincipal Long userCode) {
    	return ResponseEntity.ok().body(mypageService.getMyPage(userCode));
    }
	
	// 사용자 프로필 사진, 닉네임 변경
    @PutMapping(value = "/user", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<Void> updateUserInfo(
            @AuthenticationPrincipal Long userCode,
            @RequestPart("profileImage") MultipartFile profileImage,
            @RequestPart("kakaoNickname") String kakaoNickname) {
    	
    	mypageService.updateUserInfo(userCode, profileImage, kakaoNickname);
        return ResponseEntity.ok().build();
    }
    
    // 장바구니에 상품 추기
    @PostMapping("/cart")
    public ResponseEntity<Void> addWatchToCart(@AuthenticationPrincipal Long userCode, @RequestBody CartDto cartDto) {
        mypageService.addWatchToCart(userCode, cartDto);
        return ResponseEntity.ok().build();
    }

    // 장바구니에 상품 수량 변경
    @PutMapping("/cart/{cartId}")
    public ResponseEntity<Void> updateWatchInCart(@AuthenticationPrincipal Long userCode, @PathVariable("cartId") Long cartId, @RequestBody CartDto cartDto) {
    	mypageService.updateWatchInCart(userCode, cartId, cartDto);
        return ResponseEntity.ok().build();
    }

    // 장바구니에 상품 삭제
    @DeleteMapping("/cart/{cartId}")
    public ResponseEntity<Void> deleteWatchFromCart(@AuthenticationPrincipal Long userCode, @PathVariable("cartId") Long cartId) {
        mypageService.deleteWatchFromCart(userCode, cartId);
        return ResponseEntity.ok().build();
    }
    
    // 상품 여러개 동시 구매 (장바구니에서)
    @PostMapping("/purchases")
    public ResponseEntity<Void> purchaseItems(@AuthenticationPrincipal Long userCode, @RequestBody List<Long> cartIds) {
        mypageService.purchaseItems(userCode, cartIds);
        return ResponseEntity.ok().build();
    }
    
    // 상품 단독으로 구매 (상품 설명 페이지에서)
    @PostMapping("/purchase-direct")
    public ResponseEntity<Void> purchaseDirect(@AuthenticationPrincipal Long userCode, @RequestBody CartDto cartDto) {
        mypageService.purchaseDirect(userCode, cartDto);
        return ResponseEntity.ok().build();
    }
    
    // 최근 구매한 내역 조회
    @GetMapping("/purchases")
    public ResponseEntity<Map<LocalDateTime, List<CartDto>>> getPurchaseHistory(@AuthenticationPrincipal Long userCode) {
    	Map<LocalDateTime, List<CartDto>> purchaseHistory = mypageService.getPurchaseHistory(userCode);
        return ResponseEntity.ok().body(purchaseHistory);
    }
    
    // 최근 작성한 댓글 목록 조회
    @GetMapping("/comments")
    public ResponseEntity<List<CommentDto>> getRecentComments(@AuthenticationPrincipal Long userCode) {
        List<CommentDto> recentComments = mypageService.getRecentComments(userCode);
        return ResponseEntity.ok().body(recentComments);
    }

}
