package com.fantasy.fm.service.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.fantasy.fm.pojo.domain.entity.MusicFileInfo;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

@Mapper
public interface MusicManagerMapper extends BaseMapper<MusicFileInfo> {

    /**
     * 根据音乐ID查询音乐URL
     *
     * @param musicId 音乐ID
     * @return 音乐文件存放URL
     */
    @Select("SELECT file_path FROM music_file_info WHERE music_id = #{musicId}")
    String selectFileUrlByMusicId(Long musicId);
}
