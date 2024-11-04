package com.timetrove.Project.dto;

import com.timetrove.Project.domain.Watch;
import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class WatchDto {
    private Long id;

    @NonNull
    private String name;

    private String image;

    private int consumerPrice;

    @NonNull
    private Long soldPrice;

    @NonNull
    private Long quantity;

    private String points;

    private String discount;

    @NonNull
    private String model;

    private String[] dimagesArray;

    public static WatchDto convertWatchToDto(Watch watch) {
        String[] dimagesArray = watch.getDimagesString() != null && watch.getDimagesString().contains("|")
                ? watch.getDimagesString().split("\\|")
                : new String[]{watch.getDimagesString()};

        return WatchDto.builder()
                .id(watch.getId())
                .name(watch.getName())
                .image(watch.getImage())
                .consumerPrice(watch.getConsumerPrice())
                .soldPrice(watch.getSoldPrice())
                .quantity(watch.getQuantity())
                .points(watch.getPoints())
                .discount(watch.getDiscount())
                .model(watch.getModel())
                .dimagesArray(dimagesArray)
                .build();
    }
}
