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
    private Long watchId;
    private String watchName;
    private String watchImage;
	private Long watchPrice;
	private String watchModel;
	
    private Long quantity;
    private boolean purchased;
    private LocalDateTime purchaseDate;
    private Long totalPrice;


    public static CartDto convertCartToDto(Cart cart) {
        CartDto cartDto = new CartDto();
        cartDto.setId(cart.getId());
        cartDto.setWatchId(cart.getWatch().getId());
        cartDto.setWatchName(cart.getWatch().getName());
        cartDto.setWatchImage(cart.getWatch().getImage());
        cartDto.setWatchPrice(cart.getWatch().getSoldPrice());
        cartDto.setWatchModel(cart.getWatch().getModel());
        cartDto.setQuantity(cart.getQuantity());
        cartDto.setPurchased(cart.isPurchased());
        cartDto.setPurchaseDate(cart.getPurchaseDate());
        cartDto.setTotalPrice(cart.getQuantity() * cart.getWatch().getSoldPrice());
        return cartDto;
    }
    
    public static List<CartDto> convertCartListToDto(List<Cart> cartList) {
        return cartList.stream()
                .map(CartDto::convertCartToDto)
                .collect(Collectors.toList());
    }
}
