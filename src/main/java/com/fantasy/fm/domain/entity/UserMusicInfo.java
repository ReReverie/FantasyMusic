package com.fantasy.fm.domain.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
@TableName("user_music_info")
public class UserMusicInfo {
    private Long id;
    private Long userId; // 用户ID
    private List<Music> musicList; // 用户的音乐列表
}
