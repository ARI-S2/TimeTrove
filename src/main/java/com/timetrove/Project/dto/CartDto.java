package com.timetrove.Project.dto;

import com.timetrove.Project.domain.Cart;
import lombok.Builder;
import lombok.Data;
import lombok.NonNull;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

import lombok.AllArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CartDto {
    private Long id;
    private Long userCode;
    @NonNull
    private Long watchNo;
    private String watchName;
    private String watchImage;
	private int watchPrice;
	private String watchModel;
	
    private int quantity;
    private boolean purchased;
    private LocalDateTime purchaseDate;
    private int totalPrice;

    public static CartDto convertCartToDto(Cart cart) {
        CartDto cartDto = new CartDto();
        cartDto.setId(cart.getId());
        cartDto.setWatchNo(cart.getWatch().getNo());
        cartDto.setWatchName(cart.getWatch().getName());
        cartDto.setWatchImage(cart.getWatch().getImage());
        cartDto.setWatchPrice(cart.getWatch().getS_price());
        cartDto.setWatchModel(cart.getWatch().getModel());
        cartDto.setQuantity(cart.getQuantity());
        cartDto.setPurchased(cart.isPurchased());
        cartDto.setPurchaseDate(cart.getPurchaseDate());
        cartDto.setTotalPrice(cart.getQuantity() * cart.getWatch().getS_price());
        return cartDto;
    }
    
    public static List<CartDto> convertCartListToDto(List<Cart> cartList) {
        return cartList.stream()
                .map(CartDto::convertCartToDto)
                .collect(Collectors.toList());
    }
}
