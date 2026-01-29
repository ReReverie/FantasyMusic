package com.fantasy.fm.domain.query;

import lombok.Data;

@Data
public class MusicPageQuery {
    private Integer pageNum = 1;
    private Integer pageSize = 20;
}
