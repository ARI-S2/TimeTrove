package com.timetrove.Project.controller;

import com.timetrove.Project.dto.PageResponse;
import com.timetrove.Project.dto.ProductDto;
import com.timetrove.Project.service.ProductService;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;


@RestController
@RequestMapping("/api/products")
@RequiredArgsConstructor
public class ProductController {

	private final ProductService productService;

	/**
	 * @param page 요청 페이지 번호
	 * @param searchWord 검색어 (옵션)
	 * @param filter 필터 조건 (옵션)
	 * @return 페이지별 상품 목록
	 */
	@Operation(summary = "상품 목록 조회", description = "페이지 번호, 검색어, 필터 조건에 따라 상품 목록을 조회하여 반환합니다.")
	@GetMapping
	public ResponseEntity<PageResponse<ProductDto>> getProductList(
			@RequestParam(defaultValue = "1") int page,
			@RequestParam(required = false) String searchWord,
			@RequestParam(required = false) String filter) {

		PageResponse<ProductDto> pageResponse = productService.findProductList(page, searchWord, filter);
		return ResponseEntity.ok().body(pageResponse);
	}

	/**
	 * @param id 상품 ID
	 * @param request HttpServletRequest 객체
	 * @param response HttpServletResponse 객체
	 * @return 조회된 상품 상세 정보
	 */
	@Operation(summary = "상품 상세정보 조회", description = "ID에 해당하는 상품의 상세정보를 조회하고 조회수를 증가합니다.")
	@GetMapping("/{id}")
	public ResponseEntity<ProductDto> getProductDetail(
			@PathVariable("id") Long id,
			HttpServletRequest request,
			HttpServletResponse response) {
		return ResponseEntity.ok(productService.getProductByIdAndIncreaseViewCount(id, request, response));
	}
}
