package com.fantasy.fm.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.fantasy.fm.domain.entity.User;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface UserMapper extends BaseMapper<User> {
}
