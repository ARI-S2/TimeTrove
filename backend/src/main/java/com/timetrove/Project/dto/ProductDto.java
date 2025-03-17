package com.timetrove.Project.dto;

import com.timetrove.Project.domain.Product;
import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ProductDto {
    private Long id;

    @NonNull
    private String name;

    private String image;

    private int consumerPrice;

    @NonNull
    private Long soldPrice;

    private String discount;

    @NonNull
    private String model;

    private String[] dimagesArray;

    public static ProductDto convertProductToDto(Product product) {
        String[] dimagesArray = product.getDetailImage() != null && product.getDetailImage().contains("|")
                ? product.getDetailImage().split("\\|")
                : new String[]{product.getDetailImage()};

        return ProductDto.builder()
                .id(product.getProductId())
                .name(product.getProductName())
                .image(product.getImage())
                .consumerPrice(product.getConsumerPrice())
                .soldPrice((long) product.getPrice())
                .discount(product.getDiscountRate())
                .model(product.getModel())
                .dimagesArray(dimagesArray)
                .build();
    }
}
