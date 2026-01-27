package com.fantasy.fm.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.fantasy.fm.domain.entity.MusicFileInfo;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

@Mapper
public interface MusicManagerMapper extends BaseMapper<MusicFileInfo> {

    @Select("select * from music_file_info where music_id = #{id}")
    MusicFileInfo getFileInfoByMusicId(Long musicId);
}
