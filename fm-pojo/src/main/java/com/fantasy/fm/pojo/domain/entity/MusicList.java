package com.fantasy.fm.pojo.domain.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Builder;
import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 歌单实体类
 */
@Data
@Builder
@TableName("music_list")
public class MusicList implements Serializable {

    @TableId(type = IdType.AUTO)
    private Long id;

    /**
     * 歌单标题
     */
    private String title;

    /**
     * 歌单简介
     */
    private String description;

    /**
     * 封面图片URL
     */
    private String cover;

    /**
     * 创建者用户ID
     */
    private Long userId;

    /**
     * 创建时间
     */
    private LocalDateTime createTime;

    /**
     * 更新时间
     */
    private LocalDateTime updateTime;
}
