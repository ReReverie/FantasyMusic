package com.fantasy.fm.service.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.fantasy.fm.pojo.domain.dto.ResetPasswordDTO;
import com.fantasy.fm.pojo.domain.dto.UpdatePasswordDTO;
import com.fantasy.fm.pojo.domain.dto.UserLoginDTO;
import com.fantasy.fm.pojo.domain.dto.UserRegisterDTO;
import com.fantasy.fm.pojo.domain.entity.User;
import com.fantasy.fm.pojo.domain.vo.UserInfoVO;


public interface UserService extends IService<User> {
    /**
     * 用户登录
     * @param userLoginDTO 用户登录传输对象
     * @return 登录成功的用户信息
     */
    User login(UserLoginDTO userLoginDTO);

    /**
     * 用户注册
     * @param userRegisterDTO 用户注册传输对象
     */
    void register(UserRegisterDTO userRegisterDTO);

    /**
     * 获取用户信息
     * @param userId 用户ID
     * @return 用户信息视图对象
     */
    UserInfoVO getUserInfo(Long userId);

    /**
     * 更新用户信息
     * @param userInfoVO 用户信息视图对象
     */
    void updateUserInfo(UserInfoVO userInfoVO);

    /**
     * 更新用户密码
     * @param updatePasswordDTO 更新密码传输对象
     */
    void updatePassword(UpdatePasswordDTO updatePasswordDTO);

    /**
     * 重置用户密码
     * @param resetPasswordDTO 重置密码传输对象
     */
    void resetPassword(ResetPasswordDTO resetPasswordDTO);
}
