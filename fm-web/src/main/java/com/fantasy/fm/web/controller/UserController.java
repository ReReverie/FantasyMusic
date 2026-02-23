package com.fantasy.fm.web.controller;

import com.fantasy.fm.common.constant.AuthConstant;
import com.fantasy.fm.common.context.BaseContext;
import com.fantasy.fm.common.properties.JwtProperties;
import com.fantasy.fm.common.response.Result;
import com.fantasy.fm.common.utils.JwtUtil;
import com.fantasy.fm.pojo.domain.dto.UpdatePasswordDTO;
import com.fantasy.fm.pojo.domain.dto.UserLoginDTO;
import com.fantasy.fm.pojo.domain.dto.UserRegisterDTO;
import com.fantasy.fm.pojo.domain.entity.User;
import com.fantasy.fm.pojo.domain.vo.UserInfoVO;
import com.fantasy.fm.pojo.domain.vo.UserLoginVO;
import com.fantasy.fm.service.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;

@Slf4j
@RestController
@RequestMapping("/user")
@RequiredArgsConstructor
@Tag(name = "用户管理", description = "用户登录、注册、信息查询等接口")
public class UserController {

    private final UserService userService;
    private final JwtProperties jwtProperties;

    /**
     * 用户登录接口
     */
    @Operation(summary = "用户登录", description = "用户使用用户名和密码进行登录")
    @PostMapping("/login")
    public Result<UserLoginVO> login(@RequestBody UserLoginDTO userLoginDTO) {
        log.info("用户登录：{}", userLoginDTO);
        User user = userService.login(userLoginDTO);
        // 程序执行到这里说明登录成功
        //构造JWT令牌的载荷
        HashMap<String, Object> claims = new HashMap<>();
        claims.put(AuthConstant.USER_ID, user.getId());
        //登录成功生成JWT令牌
        String token = JwtUtil
                .generateToken(
                        jwtProperties.getSecretKey(),
                        jwtProperties.getExpireTime(),
                        claims
                );

        //构建登录响应结果
        UserLoginVO result = UserLoginVO.builder()
                .id(user.getId())
                .username(user.getUsername())
                .nickname(user.getNickname())
                .token(token)
                .build();

        return Result.success(result);
    }

    /**
     * 用户注册接口
     */
    @Operation(summary = "用户注册", description = "用户使用用户名和密码进行注册")
    @PostMapping("/register")
    public Result<Void> register(@RequestBody UserRegisterDTO userRegisterDTO) {
        log.info("用户注册：{}", userRegisterDTO);
        userService.register(userRegisterDTO);
        return Result.success();
    }

    /**
     * 获取当前用户信息
     */
    @Operation(summary = "获取当前用户信息", description = "获取当前登录用户的详细信息")
    @GetMapping("/info")
    public Result<UserInfoVO> getUserInfo() {
        return Result.success(userService
                .getUserInfo(BaseContext.getCurrentId()));
    }

    /**
     * 修改个人信息
     */
    @Operation(summary = "修改个人信息", description = "修改当前登录用户的个人信息")
    @PutMapping("/update")
    public Result<Void> updateUserInfo(@RequestBody UserInfoVO userInfoVO) {
        userService.updateUserInfo(userInfoVO);
        return Result.success();
    }

    /**
     * 修改用户密码
     */
    @Operation(summary = "修改用户密码", description = "修改当前登录用户的密码")
    @PutMapping("/password")
    public Result<Void> updatePassword(@RequestBody UpdatePasswordDTO updatePasswordDTO) {
        userService.updatePassword(updatePasswordDTO);
        return Result.success();
    }

    /**
     * 退出登录接口
     */
    @Operation(summary = "退出登录", description = "用户退出登录接口")
    @PostMapping("/logout")
    public Result<String> logout() {
        return Result.success(AuthConstant.LOGOUT_SUCCESS);
    }
}
