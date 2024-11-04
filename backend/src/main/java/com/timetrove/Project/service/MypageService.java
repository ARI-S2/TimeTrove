package com.timetrove.Project.service;


import java.util.HashMap;
import java.util.Map;

import com.timetrove.Project.dto.CartDto;
import com.timetrove.Project.dto.user.UserDto;
import com.timetrove.Project.repository.CartRepository;
import com.timetrove.Project.repository.UserRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;

@Service
@Slf4j
@RequiredArgsConstructor
public class MypageService {

	private final UserRepository userRepository;
	private final CartRepository cartRepository;

	/**
	 * 마이페이지 정보 조회 메서드
	 * 사용자 정보와 장바구니 목록을 조회하여 반환
	 * @param: Long userCode - 사용자 코드
	 * @return 사용자 정보와 장바구니 목록을 포함한 맵 객체
	 */
	public Map<String, Object> getMyPage(Long userCode) {
		Map<String, Object> map = new HashMap<>();
		map.put("userinfo", UserDto.convertUserToDto(userRepository.findByUserCode(userCode)));
		map.put("cartList", CartDto.convertCartListToDto(
				cartRepository.findByUser_UserCodeAndPurchasedOrderByPurchaseDateDesc(userCode, false)));
		return map;
	}

}
