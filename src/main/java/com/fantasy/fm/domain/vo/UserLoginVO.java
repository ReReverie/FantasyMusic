package com.fantasy.fm.domain.vo;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class UserLoginVO {
    private Long id;
    private String username;
    private String nickname;
    private String token;
}
