package com.fantasy.fm.pojo.domain.query;

import lombok.Data;

@Data
public class MusicPageQuery{
    private Long pageNum = 1L;
    private Long pageSize = 20L;
    private String keyword;

    public int from() {
        return (int) ((pageNum - 1) * pageSize);
    }
}
