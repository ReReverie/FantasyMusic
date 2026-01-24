package com.fantasy.fm.pojo.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

/**
 * 用于表示音乐信息的实体类
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@TableName("music")
public class Music {
    private Long id;
    private String name; // 歌曲名
    private String title; // 歌曲标题
    private String artist; // 歌手
    private String album; // 专辑
    private Long durationMs; // 歌曲时长(毫秒表示)
    private LocalDate releaseDate; // 发行日期
}
