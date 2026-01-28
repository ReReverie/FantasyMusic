package com.fantasy.fm.domain.dto;

import lombok.Data;

@Data
public class AddMusicToListDTO {
    private Long musicListId;
    private Long musicId;
}
