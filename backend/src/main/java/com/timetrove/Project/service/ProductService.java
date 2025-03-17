package com.timetrove.Project.service;

import com.timetrove.Project.common.enumType.ErrorCode;
import com.timetrove.Project.common.exception.EntityNotFoundException;
import com.timetrove.Project.domain.Product;
import com.timetrove.Project.dto.PageResponse;
import com.timetrove.Project.dto.ProductDto;
import com.timetrove.Project.repository.querydsl.ProductRepository;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

@Service
@RequiredArgsConstructor
public class ProductService {

	private final ProductRepository productRepository;
	private final RankingService rankingService;

	private static final int PAGE_SIZE = 6;

	/**
	 * 상품 조회 및 조회수 증가 메서드
	 * @param: Long id - 상품 ID
	 * @return ProductDto
	 */
	@Transactional
	public ProductDto getProductByIdAndIncreaseViewCount(Long id, HttpServletRequest request, HttpServletResponse response) {
		Product Product = findProductByIdOrThrow(id);

		// 쿠키를 확인하여 조회수 중복 방지 처리
		if (!isProductAlreadyViewed(id, request)) {
			// 레디스에 조회수 증가 처리
			rankingService.updateProductScore(id);
			// 새로운 쿠키 생성
			addProductViewCookie(id, response);
		}

		return ProductDto.convertProductToDto(Product);
	}

	private boolean isProductAlreadyViewed(Long id, HttpServletRequest request) {
		Cookie[] cookies = request.getCookies();
		if (cookies != null) {
			for (Cookie cookie : cookies) {
				if (cookie.getName().equals("ProductView") && cookie.getValue().contains("[" + id + "]")) {
					return true;
				}
			}
		}
		return false;
	}

	private void addProductViewCookie(Long id, HttpServletResponse response) {
		Cookie cookie = new Cookie("ProductView", "[" + id + "]");
		cookie.setPath("/");
		cookie.setHttpOnly(true);
		cookie.setMaxAge(24 * 60 * 60); // 1일 동안 유지
		response.addCookie(cookie);
	}

	/**
	 * 상품 리스트 조회 메서드
	 * 페이지 번호, 검색어, 필터를 기준으로 상품 리스트를 조회
	 * @param: int page - 페이지 번호
	 * @param: String searchWord - 검색어
	 * @param: String filter - 필터 조건
	 * @return 페이징된 ProductDto 리스트
	 */
	@Transactional
	public PageResponse<ProductDto> findProductList(int page, String searchWord, String filter) {
		Pageable pageable = PageRequest.of(page - 1, PAGE_SIZE);
		Page<Product> ProductPage = productRepository.findProductsWithFilter(searchWord, filter, pageable);
		return PageResponse.fromPage(ProductPage.map(ProductDto::convertProductToDto));
	}

	/**
	 * 상품 조회 메서드
	 * 상품이 없을 시 예외 발생 (PRODUCT_NOT_FOUND)
	 * @param: Long id - 상품 ID
	 * @return PRODUCT
	 */
	private Product findProductByIdOrThrow(Long id) {
		return productRepository.findById(id)
				.orElseThrow(() -> new EntityNotFoundException(HttpStatus.NOT_FOUND, ErrorCode.PRODUCT_NOT_FOUND));
	}
}
