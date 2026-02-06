package com.fantasy.fm.service.impl;

import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.fantasy.fm.constant.RedisCacheConstant;
import com.fantasy.fm.context.BaseContext;
import com.fantasy.fm.domain.dto.ResetPasswordDTO;
import com.fantasy.fm.domain.dto.UserRegisterDTO;
import com.fantasy.fm.exception.*;
import com.fantasy.fm.constant.AuthConstant;
import com.fantasy.fm.domain.dto.UpdatePasswordDTO;
import com.fantasy.fm.domain.dto.UserLoginDTO;
import com.fantasy.fm.domain.entity.User;
import com.fantasy.fm.domain.vo.UserInfoVO;
import com.fantasy.fm.mapper.UserMapper;
import com.fantasy.fm.service.UserService;
import com.fantasy.fm.utils.RedisCacheUtil;
import com.fantasy.fm.utils.security.PasswordUtil;
import com.fantasy.fm.utils.security.RSADecryptor;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.concurrent.TimeUnit;

@Service
@Slf4j
@RequiredArgsConstructor
public class UserServiceImpl extends ServiceImpl<UserMapper, User> implements UserService {

    private final RSADecryptor rsaDecryptor;
    private final RedisCacheUtil redisCacheUtil;

    @Override
    public User login(UserLoginDTO userLoginDTO) {
        String username = userLoginDTO.getUsername();
        //构建redis的key
        String redisKey = RedisCacheConstant.LOGIN_FAIL + "::" + username;
        //获取登录失败次数
        Integer failCount = redisCacheUtil.get(redisKey, Integer.class);
        //如果failCount为空,表示用户第一次进行登录操作,将失败次数设置为1
        if (failCount != null && failCount >= 3) {
            //如果到这表示失败次数过多,强制检查验证码
            if (userLoginDTO.getCaptchaCode() == null || userLoginDTO.getCaptchaCode().isEmpty() || userLoginDTO.getCaptchaUuid() == null) {
                throw new CaptchaRequiredException(AuthConstant.NEED_CAPTCHA);
            }
            //校验验证码有效性
            String redisCaptchaKey = RedisCacheConstant.LOGIN_CAPTCHA + "::" + userLoginDTO.getCaptchaUuid();
            String realCaptchaKey = redisCacheUtil.get(redisCaptchaKey, String.class);
            //如果验证码错误，抛出异常
            if (realCaptchaKey == null || !realCaptchaKey.equalsIgnoreCase(userLoginDTO.getCaptchaCode())) {
                log.info("用户登录失败，验证码错误：{}", userLoginDTO);
                throw new CaptchaErrorException(AuthConstant.CODE_INVALID);
            }
            //验证码校验通过, 删除缓存中的验证码
            redisCacheUtil.delete(redisCaptchaKey);
        }

        //根据用户名查询用户信息
        User user = this.lambdaQuery()
                .eq(User::getUsername, username)
                .one();

        if (user == null) {
            log.info("用户登录失败，用户未找到：{}", userLoginDTO);
            //记录登录失败次数
            incrementFailCount(redisKey);
            throw new UserNotFoundException(AuthConstant.USER_NOT_FOUND);
        }

        //如果执行到这里，说明用户存在，验证密码
        String password = getDecryptPassword(userLoginDTO.getPassword());

        //验证密码是否正确
        if (!PasswordUtil.matches(password, user.getPassword())) {
            log.info("用户登录失败，密码错误：{}", userLoginDTO);
            //记录登录失败次数
            incrementFailCount(redisKey);
            throw new PasswordErrorException(AuthConstant.ERROR_PASSWORD);
        }

        //如果登录成功,删除失败缓存
        //只有登录成功了，才把计数器清零，否则用户下次还得输验证码（直到过期）
        redisCacheUtil.delete(redisKey);

        return user;
    }

    /**
     * 记录登录失败次数
     *
     * @param redisKey redis的key
     */
    private void incrementFailCount(String redisKey) {
        Integer count = redisCacheUtil.get(redisKey, Integer.class);
        if (count == null) {
            count = 1;
        } else {
            count++;
        }
        redisCacheUtil.set(redisKey, count, 15L, TimeUnit.MINUTES); // 设置10分钟过期时间
    }

    @Override
    public void register(UserRegisterDTO userRegisterDTO) {
        //检查邮箱是否已经被注册过
        Long count = this.lambdaQuery()
                .eq(User::getEmail, userRegisterDTO.getEmail())
                .count();

        if (count > 0) {
            log.info("用户注册失败，邮箱已被注册：{}", userRegisterDTO);
            throw new EmailAlreadyRegisteredException(AuthConstant.EMAIL_ALREADY_REGISTERED);
        }

        String username = userRegisterDTO.getUsername();
        String password = getDecryptPassword(userRegisterDTO.getPassword());

        //对用户名的合法性进行校验:不少于 3 位，不能是纯数字
        if (!username.matches(AuthConstant.USERNAME_REGEX)) {
            log.info("用户注册失败，用户名不合法：{}", userRegisterDTO);
            throw new UsernameInvalidException(AuthConstant.USERNAME_INVALID);
        }

        //对密码的合法性进行校验:8–24 位，必须包含大小写字母，允许特殊字符
        if (!password.matches(AuthConstant.PASSWORD_REGEX)) {
            log.info("用户注册失败，密码不合法：{}", userRegisterDTO);
            throw new PasswordInvalidException(AuthConstant.INVALID_PASSWORD);
        }

        User existingUser = this.lambdaQuery()
                .eq(User::getUsername, username)
                .one();

        if (existingUser != null) {
            log.info("用户注册失败，用户名已存在：{}", userRegisterDTO);
            throw new UserAlreadyExistsException(AuthConstant.USER_ALREADY_EXISTS);
        }

        //获取用户输入的邮箱和验证码
        String email = userRegisterDTO.getEmail();
        String emailCode = userRegisterDTO.getEmailCode();

        if (StrUtil.hasBlank(email, emailCode)) {
            throw new EmailInvalidException(AuthConstant.INPUT_EMPTY);
        }
        //从Redis中获取验证码
        String redisKey = RedisCacheConstant.EMAIL_CODE + "::" + email;
        String realCode = redisCacheUtil.get(redisKey, String.class);
        //判断验证码是否正确或失效
        if (realCode == null || !realCode.equals(emailCode)) {
            throw new EmailCodeErrorException(AuthConstant.CODE_WRONG);
        }
        //从缓存中删除验证码
        redisCacheUtil.delete(redisKey);

        //对用户密码进行加密处理
        String encodePw = PasswordUtil.encodePassword(password);

        User user = new User();
        user.setUsername(username);
        user.setPassword(encodePw);
        user.setEmail(email);
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
            throw new LoginException(AuthConstant.LOGIN_EXCEPTION);
        }
        return password;
    }

    @Override
    @Cacheable(cacheNames = RedisCacheConstant.USER_INFO_DATA, key = "#userId")
    public UserInfoVO getUserInfo(Long userId) {
        log.info("获取用户信息，用户ID：{}", userId);
        UserInfoVO userInfoVO = new UserInfoVO();
        BeanUtils.copyProperties(this.getById(userId), userInfoVO);
        return userInfoVO;
    }

    @Override
    @CacheEvict(cacheNames = RedisCacheConstant.USER_INFO_DATA, key = "#userInfoVO.id")
    public void updateUserInfo(UserInfoVO userInfoVO) {
        log.info("更新用户信息：{}", userInfoVO);
        User user = new User();
        user.setId(BaseContext.getCurrentId());
        user.setNickname(userInfoVO.getNickname());
        user.setAvatarUrl(userInfoVO.getAvatarUrl());
        //如果邮箱不为空，则进行更新
        if (userInfoVO.getEmail() != null && !userInfoVO.getEmail().isEmpty()) {
            //检验邮箱是否合法
            if (!userInfoVO.getEmail().matches(AuthConstant.EMAIL_REGEX)) {
                throw new EmailInvalidException(AuthConstant.INVALID_EMAIL);
            }
            user.setEmail(userInfoVO.getEmail());
        } else {
            throw new EmailNotAllowedEmptyException(AuthConstant.EMAIL_NOT_ALLOW_EMPTY);
        }
        user.setUpdateTime(LocalDateTime.now());
        this.updateById(user);
    }

    @Override
    public void updatePassword(UpdatePasswordDTO updatePasswordDTO) {
        Long userId = BaseContext.getCurrentId();
        String oldPassword = updatePasswordDTO.getOldPassword();
        String newPassword = updatePasswordDTO.getNewPassword();
        //获取当前用户信息
        User user = this.getById(userId);

        //解密旧密码
        oldPassword = getDecryptPassword(oldPassword);

        //验证旧密码是否正确
        if (!PasswordUtil.matches(oldPassword, user.getPassword())) {
            log.info("更新用户密码失败，旧密码错误：{}", updatePasswordDTO);
            throw new PasswordErrorException(AuthConstant.ERROR_PASSWORD);
        }

        //解密新密码
        newPassword = getDecryptPassword(newPassword);

        //对新密码进行加密处理
        //先判断新密码是否合法:8–24 位，必须包含大小写字母，允许特殊字符
        if (!newPassword.matches(AuthConstant.PASSWORD_REGEX)) {
            log.info("更新用户密码失败，新密码不合法：{}", updatePasswordDTO);
            throw new PasswordInvalidException(AuthConstant.INVALID_PASSWORD);
        }

        //防止新密码与旧密码相同
        if (PasswordUtil.matches(newPassword, user.getPassword())) {
            log.info("更新用户密码失败，新密码与旧密码相同：{}", updatePasswordDTO);
            throw new NewPasswordSameAsOldException(AuthConstant.NEW_PASSWORD_OLD_SAME);
        }

        //对新密码进行加密处理
        String encodeNewPassword = PasswordUtil.encodePassword(newPassword);

        //更新用户密码
        user.setPassword(encodeNewPassword);
        user.setUpdateTime(LocalDateTime.now());
        this.updateById(user);
    }

    @Override
    public void resetPassword(ResetPasswordDTO resetPasswordDTO) {
        //获取用户信息
        String account = resetPasswordDTO.getAccount();
        User entity = this.lambdaQuery()
                .eq(User::getUsername, account)
                .or()
                .eq(User::getEmail, account)
                .one();
        //如果没找到用户，抛出校验失败异常
        if (entity == null) {
            throw new UserValidationException(AuthConstant.USER_VALIDATION_FAILED);
        }
        //从Redis中获取验证码
        String redisKey = RedisCacheConstant.RESET_EMAIL_CODE + "::" + entity.getId();
        String realCode = redisCacheUtil.get(redisKey, String.class);

        //判断验证码是否正确或失效
        if (realCode == null || !realCode.equals(resetPasswordDTO.getCode())) {
            throw new EmailCodeErrorException(AuthConstant.CODE_WRONG);
        }

        //对新密码进行加密处理
        String decryptPassword = getDecryptPassword(resetPasswordDTO.getNewPassword()); //解密密码

        //先判断新密码是否合法:8–24 位，必须包含大小写字母，允许特殊字符
        if (!decryptPassword.matches(AuthConstant.PASSWORD_REGEX)) {
            throw new PasswordInvalidException(AuthConstant.INVALID_PASSWORD);
        }
        //加密成数据库密码
        String encodeNewPassword = PasswordUtil.encodePassword(decryptPassword);

        //更新用户密码
        entity.setPassword(encodeNewPassword);
        entity.setUpdateTime(LocalDateTime.now());
        this.updateById(entity);
    }
}
