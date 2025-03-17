package com.timetrove.Project.dto;


import lombok.*;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RankedListDto {
    private List<ProductDto> topProductList;
    private List<BoardDto> topBoardList;
}