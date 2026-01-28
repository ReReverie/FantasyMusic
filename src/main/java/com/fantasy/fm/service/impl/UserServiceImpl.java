package com.fantasy.fm.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.fantasy.fm.Exception.PasswordInvalidException;
import com.fantasy.fm.Exception.UserAlreadyExistsException;
import com.fantasy.fm.Exception.UserNotFoundException;
import com.fantasy.fm.constant.LoginConstant;
import com.fantasy.fm.domain.dto.UserLoginDTO;
import com.fantasy.fm.domain.entity.User;
import com.fantasy.fm.domain.vo.UserInfoVO;
import com.fantasy.fm.mapper.UserMapper;
import com.fantasy.fm.service.UserService;
import com.fantasy.fm.utils.PasswordUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class UserServiceImpl extends ServiceImpl<UserMapper, User> implements UserService {

    @Override
    public User login(UserLoginDTO userLoginDTO) {
        String username = userLoginDTO.getUsername();
        String password = userLoginDTO.getPassword();

        User user = this.lambdaQuery()
                .eq(User::getUsername, username)
                .one();

        if (user == null) {
            log.info("用户登录失败，用户未找到：{}", userLoginDTO);
            throw new UserNotFoundException(LoginConstant.USER_NOT_FOUND);
        }

        //验证密码是否正确
        if (!PasswordUtil.matches(password, user.getPassword())) {
            log.info("用户登录失败，密码错误：{}", userLoginDTO);
            throw new PasswordInvalidException(LoginConstant.INVALID_PASSWORD);
        }

        return user;
    }

    @Override
    public void register(UserLoginDTO userLoginDTO) {
        String username = userLoginDTO.getUsername();
        String password = userLoginDTO.getPassword();

        User existingUser = this.lambdaQuery()
                .eq(User::getUsername, username)
                .one();

        if (existingUser != null) {
            log.info("用户注册失败，用户名已存在：{}", userLoginDTO);
            throw new UserAlreadyExistsException(LoginConstant.USER_ALREADY_EXISTS);
        }
        //对用户密码进行加密处理
        String encodePw = PasswordUtil.encodePassword(password);

        //保存用户信息到数据库
        this.save(User.builder()
                .username(username)
                .password(encodePw)
                .build());
    }

    @Override
    public UserInfoVO getUserInfo(Long userId) {
        UserInfoVO userInfoVO = new UserInfoVO();
        BeanUtils.copyProperties(this.getById(userId), userInfoVO);
        return userInfoVO;
    }
}
