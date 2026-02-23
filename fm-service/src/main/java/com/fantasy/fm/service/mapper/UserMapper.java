package com.fantasy.fm.service.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.fantasy.fm.pojo.domain.entity.User;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface UserMapper extends BaseMapper<User> {
}
