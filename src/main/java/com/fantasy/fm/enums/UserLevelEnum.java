package com.fantasy.fm.enums;

import com.baomidou.mybatisplus.annotation.EnumValue;
import com.fasterxml.jackson.annotation.JsonValue;
import lombok.Getter;

import java.util.Arrays;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 用户等级枚举
 */
@Getter
public enum UserLevelEnum {
    NORMAL(0, "普通用户"),
    VIP(1, "VIP用户"),
    SVIP(2, "超级VIP用户"),
    ADMIN(3, "管理员用户");

    @EnumValue  // 存入数据库时使用的值
    private final int levelCode;
    @JsonValue // 返回给前端时使用描述信息
    private final String description;

    UserLevelEnum(int levelCode, String description) {
        this.levelCode = levelCode;
        this.description = description;
    }

    // 缓存提升查找性能
    private static final Map<Integer, UserLevelEnum> CODE_MAP = Arrays.stream(values())
            .collect(Collectors.toMap(UserLevelEnum::getLevelCode, e -> e));

    /**
     * 根据 code 获取枚举
     */
    public static UserLevelEnum getByCode(Integer code) {
        return code == null ? null : CODE_MAP.get(code);
    }

    /**
     * 根据 code 获取描述信息
     */
    public static String getDescByCode(Integer code) {
        UserLevelEnum levelEnum = getByCode(code);
        return levelEnum != null ? levelEnum.getDescription() : null;
    }

    /**
     * 判断枚举是否存在
     */
    public static Boolean hasLevel(UserLevelEnum userLevelEnum) {
        for (UserLevelEnum value : values()) {
            if (value == userLevelEnum) {
                return true;
            }
        }
        return false;
    }

    /**
     * 判断用户等级是否达到达到管理员级别
     */
    public static Boolean isAdmin(UserLevelEnum userLevelEnum) {
        return userLevelEnum == ADMIN;
    }

    /**
     * 判断用户等级是否达到SVIP及以上级别
     */
    public static Boolean isSvipOrAbove(UserLevelEnum userLevelEnum) {
        return userLevelEnum.levelCode >= SVIP.levelCode;
    }

    /**
     * 判断用户等级是否达到VIP及以上级别
     */
    public static Boolean isVipOrAbove(UserLevelEnum userLevelEnum) {
        return userLevelEnum.levelCode >= VIP.levelCode;
    }
}
