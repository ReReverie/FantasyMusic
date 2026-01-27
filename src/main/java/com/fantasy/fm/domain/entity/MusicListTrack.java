package com.fantasy.fm.domain.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@TableName("music_list_track")
public class MusicListTrack {
    @TableId(type = IdType.AUTO)
    private Long id;
    private Long musicListId; // 歌单ID
    private Long musicId;     // 音乐ID
    private LocalDateTime createTime; // 加入歌单的时间
}