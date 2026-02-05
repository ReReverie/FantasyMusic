package com.fantasy.fm.controller;

import cn.hutool.captcha.CaptchaUtil;
import cn.hutool.captcha.LineCaptcha;
import cn.hutool.core.lang.Validator;
import cn.hutool.core.util.IdUtil;
import cn.hutool.core.util.RandomUtil;
import com.fantasy.fm.constant.AuthConstant;
import com.fantasy.fm.constant.RedisCacheConstant;
import com.fantasy.fm.properties.MailProperties;
import com.fantasy.fm.response.Result;
import com.fantasy.fm.utils.RedisCacheUtil;
import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jspecify.annotations.Nullable;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

@Slf4j
@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {

    private final RedisCacheUtil redisCacheUtil;
    private final JavaMailSender javaMailSender;
    private final MailProperties mailProperties;

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
        String redisKey = RedisCacheConstant.LOGIN_CAPTCHA + "::" + uuid;
        redisCacheUtil.set(redisKey, code, 60L, TimeUnit.SECONDS);

        //组装数据返回给前端
        HashMap<String, String> hashMap = new HashMap<>();
        hashMap.put("uuid", uuid);
        //将图片转换为Base64字符串
        hashMap.put("img", lineCaptcha.getImageBase64Data());

        return Result.success(hashMap);
    }

    /**
     * 发送邮箱验证码接口
     */
    @Operation(summary = "发送验证码", description = "向用户邮箱发送验证码")
    @PostMapping("/email-code")
    public Result<Void> sendEmailCode(@RequestParam String email) {
        //校验邮箱格式是否合法
        if (!Validator.isEmail(email)) {
            return Result.error(AuthConstant.EMAIL_INVALID);
        }

        //生成6位数字验证码
        String code = RandomUtil.randomNumbers(6);

        //将验证码存入Redis, 有效期5分钟
        String redisKey = RedisCacheConstant.EMAIL_CODE + "::" + email;
        redisCacheUtil.set(redisKey, code, 5L, TimeUnit.MINUTES);

        //发送验证码到用户邮箱
        Result<Void> error = sendCode2Email(email, code);
        if (error != null) return error;

        return Result.success();
    }

    /**
     * 发送验证码到邮箱
     *
     * @param email 用户邮箱
     * @param code  验证码
     * @return 发送结果
     */
    private @Nullable Result<Void> sendCode2Email(String email, String code) {
        try {
            SimpleMailMessage message = new SimpleMailMessage();
            message.setFrom(mailProperties.getFrom());
            message.setTo(email);
            message.setSubject(mailProperties.getSubjectPrefix());
            message.setText(buildVerificationText(code));
            javaMailSender.send(message);
        } catch (Exception e) {
            log.error("邮件发送失败", e);
            return Result.error(AuthConstant.CODE_SEND_FAIL);
        }
        return null;
    }

    private String buildVerificationText(String code) {
        return String.format("您的注册验证码是：%s，有效期%d分钟。如非本人操作，请忽略。",
                code, mailProperties.getCodeExpireMinutes());
    }
}
