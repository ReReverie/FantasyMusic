package com.fantasy.fm.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.fantasy.fm.pojo.entity.MusicFileInfo;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface MusicManagerMapper extends BaseMapper<MusicFileInfo> {
}
