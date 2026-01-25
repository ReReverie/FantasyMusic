package com.fantasy.fm.domain.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * 用于表示音乐文件信息的实体类
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@TableName("music_file_info")
public class MusicFileInfo {
    private Long id;
    private Long musicId; // 音乐实体ID
    private String fileName; // 文件名
    private String filePath; // 文件路径
    private Long fileSize; // 文件大小(字节表示)
    private String fileType; // 文件类型（如mp3, wav等）
    private LocalDateTime uploadTime; // 上传时间
    private LocalDateTime createTime; // 创建时间
    private LocalDateTime updateTime; // 更新时间
}
