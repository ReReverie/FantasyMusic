package com.fantasy.fm.domain.query;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class MusicListDetailQuery {
    @JsonIgnore @NotNull
    private Long musicListId;
    @JsonIgnore @NotNull
    private Long userId;
    private String keyword;
}
