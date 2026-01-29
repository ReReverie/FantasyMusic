package com.fantasy.fm.domain.query;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;

@Data
public class MusicListPageQuery {
    @JsonIgnore
    private Long userId; // 忽略该字段，不进行序列化和反序列化
    private Long pageNum = 1L;
    private Long pageSize = 20L;
}
