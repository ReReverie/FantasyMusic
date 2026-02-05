package com.fantasy.fm.controller;

import cn.hutool.captcha.CaptchaUtil;
import cn.hutool.captcha.LineCaptcha;
import cn.hutool.core.util.IdUtil;
import com.fantasy.fm.response.Result;
import com.fantasy.fm.utils.RedisCacheUtil;
import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

@Slf4j
@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {

    private final RedisCacheUtil redisCacheUtil;

    /**
     * 生成验证码接口
     */
    @Operation(summary = "生成验证码", description = "生成登录注册使用的图形验证码")
    @GetMapping("/captcha")
    public Result<Map<String, String>> getCaptcha() {
        //生成验证码图片
        //参数：宽200，高100，字符数4，干扰线150
        LineCaptcha lineCaptcha = CaptchaUtil.createLineCaptcha(200, 100, 4, 150);

        //获取验证码里的文本内容
        String code = lineCaptcha.getCode();

        //生成UUID作为验证码的唯一标识
        String uuid = IdUtil.simpleUUID();

        //构建UUID作为验证码的redisKey存入Redis,有效期60秒
        String redisKey = "login:captcha" + "::" + uuid;
        redisCacheUtil.set(redisKey, code, 60L, TimeUnit.SECONDS);

        //组装数据返回给前端
        HashMap<String, String> hashMap = new HashMap<>();
        hashMap.put("uuid", uuid);
        //将图片转换为Base64字符串
        hashMap.put("img", lineCaptcha.getImageBase64Data());

        return Result.success(hashMap);
    }
}
