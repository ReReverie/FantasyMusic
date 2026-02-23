package com.fantasy.fm.pojo.domain.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * 用于表示音乐信息的实体类
 */
@Data
@Builder
@TableName("music")
@AllArgsConstructor
@NoArgsConstructor
public class Music implements Serializable {
    @TableId(type = IdType.AUTO)
    private Long id;
    private String title; // 歌曲标题
    private String artist; // 歌手
    private String album; // 专辑
    private Long durationMs; // 歌曲时长(毫秒表示)
    private String releaseYear; // 发行年份
    private String coverUrl; // 封面图片URL
}
