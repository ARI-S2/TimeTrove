package com.timetrove.Project.service;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import com.timetrove.Project.domain.Cart;
import com.timetrove.Project.domain.User;
import com.timetrove.Project.domain.Watch;
import com.timetrove.Project.dto.CartDto;
import com.timetrove.Project.dto.CommentDto;
import com.timetrove.Project.dto.user.UserDto;
import com.timetrove.Project.repository.CartRepository;
import com.timetrove.Project.repository.UserRepository;
import com.timetrove.Project.repository.WatchRepository;
import com.timetrove.Project.repository.querydsl.CommentRepository;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;



import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class MypageService {
	
	private final UserRepository userRepository;
	private final CartRepository cartRepository;
    private final WatchRepository watchRepository;
    private final CommentRepository commentRepository;


    // 마이페이지 메인 => 사욪자 정보, 장바구니 목록 조회
 	public Map<String, Object> getMyPage(Long userCode) {
         Map<String, Object> map = new HashMap<>();
         
 		User user = userRepository.findById(userCode).orElseThrow(() -> new RuntimeException("User not found"));
         List<Cart> cartList = cartRepository.findByUser_UserCodeAndPurchasedOrderByPurchaseDateDesc(userCode,false);
         
         map.put("userinfo", UserDto.convertUserToDto(user));
         map.put("cartList", CartDto.convertCartListToDto(cartList));
 		return map;
 	}
 	
//******** Start of 사용자 정보 *********//
    // 사용자 정보 수정
    public void updateUserInfo(Long userCode, MultipartFile profileImage, String kakaoNickname) {
        User user = userRepository.findById(userCode).orElseThrow(() -> new RuntimeException("User not found"));
        if (profileImage != null && !profileImage.isEmpty()) {
            String profileImageUrl = saveProfileImage(profileImage); // 사진 저장
            user.setKakaoProfileImg(profileImageUrl); // 사진 경로를 user에 저장
        }
        user.setKakaoNickname(kakaoNickname);
        userRepository.save(user);
    }

    // 로컬에 사진 저장 후 경로 리턴
    private String saveProfileImage(MultipartFile profileImage) {
        try {
            String folder = "uploads/";
            Path folderPath = Paths.get(folder);
            if (!Files.exists(folderPath)) {
                Files.createDirectories(folderPath);
            }
            Path filePath = folderPath.resolve(profileImage.getOriginalFilename());
            Files.write(filePath, profileImage.getBytes());
            return filePath.toString(); // Return the file path
        } catch (IOException e) {
            throw new RuntimeException("Failed to store file", e);
        }
    }

    // 사진 경로로 접근해 바이너리 데이터로 변환 후 리턴
    public byte[] getUserProfileImage(Long userCode) throws IOException {
    	User user = userRepository.findById(userCode).orElseThrow(() -> new RuntimeException("User not found"));
    	String profileImagePath = user.getKakaoProfileImg();
    	if (profileImagePath != null && !profileImagePath.isEmpty()) {
                Path imagePath = Paths.get(profileImagePath);

                if (Files.exists(imagePath)) {
                    return Files.readAllBytes(imagePath);
                }
        }
        return new byte[0]; 
    }
//******** End of 사용자 정보 *********//
    

//******** Start of 장바구니 *********//    
	public void addWatchToCart(Long userCode, CartDto cartDto) {
        User user = userRepository.findById(userCode).orElseThrow(() -> new RuntimeException("User not found"));
        Watch watch = watchRepository.findById(cartDto.getWatchNo()).orElseThrow(() -> new RuntimeException("Watch not found"));
        Optional<Cart> existingCartItem = cartRepository.findByUser_UserCodeAndWatchAndPurchasedFalse(userCode, watch);
        // 기존 장바구니 항목이 있으면 개수 업데이트
        if (existingCartItem.isPresent()) {
            Cart cart = existingCartItem.get();
            cart.setQuantity(cart.getQuantity() + cartDto.getQuantity());
            cartRepository.save(cart);
        } else {
            // 기존 항목이 없으면 새로운 장바구니 항목 추가
            Cart cart = new Cart(user, watch, cartDto.getQuantity(), false, null);
            cartRepository.save(cart);
        }
    }

    public void updateWatchInCart(Long userCode, Long cartId, CartDto cartDto) {
        Cart cart = cartRepository.findById(cartId).orElseThrow(() -> new RuntimeException("Cart item not found"));
        if (!cart.getUser().getUserCode().equals(userCode)) {
            throw new RuntimeException("Unauthorized access");
        }
        Watch watch = watchRepository.findById(cartDto.getWatchNo()).orElseThrow(() -> new RuntimeException("Watch not found"));
        cart.setWatch(watch);
        cart.setQuantity(cartDto.getQuantity());
        cartRepository.save(cart);
    }

    public void deleteWatchFromCart(Long userCode, Long cartId) {
        Cart cart = cartRepository.findById(cartId).orElseThrow(() -> new RuntimeException("Cart item not found"));
        if (!cart.getUser().getUserCode().equals(userCode)) {
            throw new RuntimeException("Unauthorized access");
        }
        cartRepository.delete(cart);
    }
//******** End of 장바구니 *********// 
    
    
//******** Start of 구매내역 *********//   
    // 상품 페이지에서 단독 상품 구매
    public void purchaseDirect(Long userCode, CartDto cartDto) {
        User user = userRepository.findById(userCode).orElseThrow(() -> new RuntimeException("User not found"));
        Watch watch = watchRepository.findById(cartDto.getWatchNo()).orElseThrow(() -> new RuntimeException("Watch not found"));
        Cart purchase = new Cart(user, watch, cartDto.getQuantity(), true, LocalDateTime.now());
        cartRepository.save(purchase);
    }
    
    // 장바구니에서 상품 구매
    public void purchaseItems(Long userCode, List<Long> cartIds) {
        userRepository.findById(userCode).orElseThrow(() -> new RuntimeException("User not found"));
        List<Cart> cartList = cartRepository.findByIdInAndUser_UserCodeAndPurchased(cartIds, userCode, false);
        for (Cart cart : cartList) {
            cart.setPurchased(true);
            cart.setPurchaseDate(LocalDateTime.now());
            cartRepository.save(cart);
        }
    }
    
    // 구메내역 날짜별로 묶어서 반환
    public Map<LocalDateTime, List<CartDto>> getPurchaseHistory(Long userCode) {
        userRepository.findById(userCode).orElseThrow(() -> new RuntimeException("User not found"));
        List<Cart> purchaseList = cartRepository.findByUser_UserCodeAndPurchasedOrderByPurchaseDateDesc(userCode, true);
        List<CartDto> cartDtos = CartDto.convertCartListToDto(purchaseList);

        return cartDtos.stream().collect(Collectors.groupingBy(CartDto::getPurchaseDate));
    }
    
    
//******** End of 구매내역 *********// 
    
    // 최근 작성한 댓글 목록 조회
    public List<CommentDto> getRecentComments(Long userCode) {
        return commentRepository.findByUserUserCodeOrderByCreatedAtDesc(userCode).stream()
                .map(CommentDto::convertCommentToDto)
                .collect(Collectors.toList());
    }
}
