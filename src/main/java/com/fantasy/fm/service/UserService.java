package com.fantasy.fm.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.fantasy.fm.domain.dto.UserLoginDTO;
import com.fantasy.fm.domain.entity.User;

public interface UserService extends IService<User> {
    /**
     * 用户登录
     * @param userLoginDTO 用户登录传输对象
     * @return 登录成功的用户信息
     */
    User login(UserLoginDTO userLoginDTO);

    /**
     * 用户注册
     * @param userLoginDTO 用户注册传输对象
     */
    void register(UserLoginDTO userLoginDTO);
}
