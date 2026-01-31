package com.fantasy.fm.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.fantasy.fm.context.BaseContext;
import com.fantasy.fm.exception.*;
import com.fantasy.fm.constant.LoginConstant;
import com.fantasy.fm.domain.dto.UpdatePasswordDTO;
import com.fantasy.fm.domain.dto.UserLoginDTO;
import com.fantasy.fm.domain.entity.User;
import com.fantasy.fm.domain.vo.UserInfoVO;
import com.fantasy.fm.mapper.UserMapper;
import com.fantasy.fm.service.UserService;
import com.fantasy.fm.utils.security.PasswordUtil;
import com.fantasy.fm.utils.security.RSADecryptor;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
@Slf4j
@RequiredArgsConstructor
public class UserServiceImpl extends ServiceImpl<UserMapper, User> implements UserService {

    private final RSADecryptor rsaDecryptor;

    @Override
    public User login(UserLoginDTO userLoginDTO) {
        String username = userLoginDTO.getUsername();
        //根据用户名查询用户信息
        User user = this.lambdaQuery()
                .eq(User::getUsername, username)
                .one();

        if (user == null) {
            log.info("用户登录失败，用户未找到：{}", userLoginDTO);
            throw new UserNotFoundException(LoginConstant.USER_NOT_FOUND);
        }

        //如果执行到这里，说明用户存在，验证密码
        String password = getDecryptPassword(userLoginDTO.getPassword());

        //验证密码是否正确
        if (!PasswordUtil.matches(password, user.getPassword())) {
            log.info("用户登录失败，密码错误：{}", userLoginDTO);
            throw new PasswordErrorException(LoginConstant.ERROR_PASSWORD);
        }

        return user;
    }

    @Override
    public void register(UserLoginDTO userLoginDTO) {
        String username = userLoginDTO.getUsername();
        String password = getDecryptPassword(userLoginDTO.getPassword());

        //对用户名的合法性进行校验:不少于 3 位，不能是纯数字
        if (!username.matches(LoginConstant.USERNAME_REGEX)) {
            log.info("用户注册失败，用户名不合法：{}", userLoginDTO);
            throw new UsernameInvalidException(LoginConstant.USERNAME_INVALID);
        }

        //对密码的合法性进行校验:8–24 位，必须包含大小写字母，允许特殊字符
        if (!password.matches(LoginConstant.PASSWORD_REGEX)) {
            log.info("用户注册失败，密码不合法：{}", userLoginDTO);
            throw new PasswordInvalidException(LoginConstant.INVALID_PASSWORD);
        }

        User existingUser = this.lambdaQuery()
                .eq(User::getUsername, username)
                .one();

        if (existingUser != null) {
            log.info("用户注册失败，用户名已存在：{}", userLoginDTO);
            throw new UserAlreadyExistsException(LoginConstant.USER_ALREADY_EXISTS);
        }
        //对用户密码进行加密处理
        String encodePw = PasswordUtil.encodePassword(password);

        User user = new User();
        user.setUsername(username);
        user.setPassword(encodePw);
        user.setCreatedTime(LocalDateTime.now());
        user.setUpdateTime(LocalDateTime.now());
        //保存用户信息到数据库
        this.save(user);
    }

    /**
     * 获取解密后的密码
     *
     * @param rsaPassword RSA加密的密码
     * @return 解密后的密码
     */
    private String getDecryptPassword(String rsaPassword) {
        //解密密码
        String password;
        try {
            password = rsaDecryptor.decrypt(rsaPassword);
        } catch (Exception e) {
            log.error("登录异常，密码解密失败：", e);
            throw new LoginException(LoginConstant.LOGIN_EXCEPTION);
        }
        return password;
    }

    @Override
    public UserInfoVO getUserInfo(Long userId) {
        log.info("获取用户信息，用户ID：{}", userId);
        UserInfoVO userInfoVO = new UserInfoVO();
        BeanUtils.copyProperties(this.getById(userId), userInfoVO);
        return userInfoVO;
    }

    @Override
    public void updateUserInfo(UserInfoVO userInfoVO) {
        log.info("更新用户信息：{}", userInfoVO);
        User user = new User();
        user.setId(BaseContext.getCurrentId());
        user.setNickname(userInfoVO.getNickname());
        user.setAvatarUrl(userInfoVO.getAvatarUrl());
        user.setEmail(userInfoVO.getEmail());
        user.setUpdateTime(LocalDateTime.now());
        this.updateById(user);
    }

    @Override
    public void updatePassword(UpdatePasswordDTO updatePasswordDTO) {
        Long userId = updatePasswordDTO.getUserId();
        String oldPassword = updatePasswordDTO.getOldPassword();
        String newPassword = updatePasswordDTO.getNewPassword();
        //获取当前用户信息
        User user = this.getById(userId);

        //验证旧密码是否正确
        if (!PasswordUtil.matches(oldPassword, user.getPassword())) {
            log.info("更新用户密码失败，旧密码错误：{}", updatePasswordDTO);
            throw new PasswordErrorException(LoginConstant.ERROR_PASSWORD);
        }

        //对新密码进行加密处理
        //先判断新密码是否合法:8–24 位，必须包含大小写字母，允许特殊字符
        if (!newPassword.matches(LoginConstant.PASSWORD_REGEX)) {
            log.info("更新用户密码失败，新密码不合法：{}", updatePasswordDTO);
            throw new PasswordInvalidException(LoginConstant.INVALID_PASSWORD);
        }

        //防止新密码与旧密码相同
        if (PasswordUtil.matches(newPassword, user.getPassword())) {
            log.info("更新用户密码失败，新密码与旧密码相同：{}", updatePasswordDTO);
            throw new NewPasswordSameAsOldException(LoginConstant.NEW_PASSWORD_OLD_SAME);
        }

        //对新密码进行加密处理
        String encodeNewPassword = PasswordUtil.encodePassword(newPassword);

        //更新用户密码
        user.setPassword(encodeNewPassword);
        user.setUpdateTime(LocalDateTime.now());
        this.updateById(user);
    }
}
