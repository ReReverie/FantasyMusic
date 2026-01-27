package com.fantasy.fm.mapper;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.fantasy.fm.domain.entity.MusicFileInfo;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

@Mapper
public interface MusicManagerMapper extends BaseMapper<MusicFileInfo> {

    /**
     * 根据音乐ID查询音乐文件信息
     *
     * @param musicId 音乐ID
     * @return 音乐文件信息
     */
    default MusicFileInfo getFileInfoByMusicId(Long musicId) {
        return this.selectOne(
                new LambdaQueryWrapper<MusicFileInfo>()
                        .eq(MusicFileInfo::getMusicId, musicId)
        );
    }
}
