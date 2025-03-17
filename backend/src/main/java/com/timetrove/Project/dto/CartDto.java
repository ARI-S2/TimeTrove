package com.timetrove.Project.dto;

import com.timetrove.Project.domain.Cart;
import com.timetrove.Project.domain.Product;
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
    private Long cartId;
    private Long userCode;
    private Long productId;
    private String productName;
    private String productImage;
    private Long price;
    private String model;
    private Long quantity;
    private Long totalPrice;
    private Long inventoryId;
    private Long productStock;
    private boolean isSoldOut;

    public static CartDto convertCartToDto(Cart cart) {
        CartDto cartDto = new CartDto();
        Product product = cart.getProductManagement().getProduct();

        cartDto.setCartId(cart.getCartId());
        cartDto.setUserCode(cart.getUser().getUserCode());
        cartDto.setProductId(product.getProductId());
        cartDto.setProductName(product.getProductName());
        cartDto.setProductImage(product.getImage());
        cartDto.setPrice(cart.getPrice());
        cartDto.setModel(product.getModel());
        cartDto.setQuantity(cart.getQuantity());
        cartDto.setTotalPrice(cart.getQuantity() * cart.getPrice());
        cartDto.setInventoryId(cart.getProductManagement().getInventoryId());
        cartDto.setProductStock(cart.getProductManagement().getQuantity());
        cartDto.setSoldOut(cart.getProductManagement().isSoldOut());

        return cartDto;
    }

    public static List<CartDto> convertCartListToDto(List<Cart> cartList) {
        return cartList.stream()
                .map(CartDto::convertCartToDto)
                .collect(Collectors.toList());
    }
}
