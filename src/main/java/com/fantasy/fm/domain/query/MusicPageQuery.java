package com.fantasy.fm.domain.query;

import lombok.Data;

@Data
public class MusicPageQuery {
    private Long pageNum = 1L;
    private Long pageSize = 20L;
    private String title;
    private String artist;
}
