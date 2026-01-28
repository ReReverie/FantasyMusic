package com.fantasy.fm.controller;

import com.fantasy.fm.constant.LoginConstant;
import com.fantasy.fm.domain.dto.UserLoginDTO;
import com.fantasy.fm.domain.entity.User;
import com.fantasy.fm.domain.vo.UserLoginVO;
import com.fantasy.fm.properties.JwtProperties;
import com.fantasy.fm.response.Result;
import com.fantasy.fm.service.UserService;
import com.fantasy.fm.utils.JwtUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;

@Slf4j
@RestController
@RequestMapping("/user")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;
    private final JwtProperties jwtProperties;

    /**
     * 用户登录接口
     */
    @PostMapping("/login")
    public Result<UserLoginVO> login(@RequestBody UserLoginDTO userLoginDTO) {
        log.info("用户登录：{}", userLoginDTO);
        User user = userService.login(userLoginDTO);
        // 程序执行到这里说明登录成功
        //构造JWT令牌的载荷
        HashMap<String, Object> claims = new HashMap<>();
        claims.put(LoginConstant.USER_ID, user.getId());
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
                .nickname(user.getNickname())
                .token(token)
                .build();

        return Result.success(result);
    }

    /**
     * 用户注册接口
     */
    @PostMapping("/register")
    public Result<Void> register(@RequestBody UserLoginDTO userLoginDTO) {
        log.info("用户注册：{}", userLoginDTO);
        userService.register(userLoginDTO);
        return Result.success();
    }
}
