package com.fantasy.fm.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.fantasy.fm.domain.entity.Music;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface MusicMapper extends BaseMapper<Music> {
}
