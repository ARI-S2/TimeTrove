package com.timetrove.Project.dto;


import lombok.*;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RankedListDto {
    private List<WatchDto> topWatchList;
    private List<BoardDto> topBoardList;
}