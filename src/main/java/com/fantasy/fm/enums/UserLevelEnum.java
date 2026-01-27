package com.fantasy.fm.enums;

import lombok.Getter;

/**
 * 用户等级枚举
 */
@Getter
public enum UserLevelEnum {
    normal(0, "普通用户"),
    vip(1, "VIP用户"),
    svip(2, "超级VIP用户");

    private final int levelCode;
    private final String description;

    UserLevelEnum(int levelCode, String description) {
        this.levelCode = levelCode;
        this.description = description;
    }
}
