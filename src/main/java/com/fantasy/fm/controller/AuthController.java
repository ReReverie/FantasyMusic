package com.fantasy.fm.controller;

import cn.hutool.captcha.CaptchaUtil;
import cn.hutool.captcha.LineCaptcha;
import cn.hutool.core.lang.Validator;
import cn.hutool.core.util.IdUtil;
import cn.hutool.core.util.RandomUtil;
import com.fantasy.fm.constant.AuthConstant;
import com.fantasy.fm.constant.RedisCacheConstant;
import com.fantasy.fm.domain.dto.ResetPasswordDTO;
import com.fantasy.fm.domain.entity.User;
import com.fantasy.fm.properties.MailProperties;
import com.fantasy.fm.response.Result;
import com.fantasy.fm.service.UserService;
import com.fantasy.fm.utils.RedisCacheUtil;
import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jspecify.annotations.NonNull;
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

    private final UserService userService;
    private final RedisCacheUtil redisCacheUtil;
    private final JavaMailSender javaMailSender;
    private final MailProperties mailProperties;
    //频率限制校验, 每分钟最多发送一次
    private String rateLimitKey = RedisCacheConstant.RATE_LIMIT_KEY + "::";

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
        rateLimitKey = rateLimitKey + email;
        if (redisCacheUtil.hasKey(rateLimitKey)) {
            return Result.error(AuthConstant.CODE_SEND_FREQUENTLY);
        }

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
        return sendCode2Email(email, code, "注册");
    }

    /**
     * 发送重置验证码接口
     */
    @Operation(summary = "发送重置验证码", description = "向用户邮箱发送重置密码的验证码")
    @PostMapping("/password/code")
    public Result<Void> sendResetEmailCode(@RequestParam String account) {
        //account 既可以是用户名，也可以是邮箱
        rateLimitKey = rateLimitKey + account;
        if (redisCacheUtil.hasKey(rateLimitKey)) {
            return Result.error(AuthConstant.CODE_SEND_FREQUENTLY);
        }
        //查询用户是否存在
        User entity = userService.lambdaQuery()
                .eq(User::getUsername, account)
                .or()
                .eq(User::getEmail, account)
                .one();
        if (entity == null) {
            //假信息,防止用户枚举攻击
            return Result.success(200, AuthConstant.FAKE_CODE_SEND_MESSAGE);
        }

        //生成6位数字验证码
        String code = RandomUtil.randomNumbers(6);

        //将验证码存入Redis, 有效期5分钟
        String redisKey = RedisCacheConstant.RESET_EMAIL_CODE + "::" + entity.getId();
        redisCacheUtil.set(redisKey, code, 5L, TimeUnit.MINUTES);

        //发送验证码到用户邮箱
        return sendCode2Email(entity.getEmail(), code, "找回");
    }

    /**
     * 重置密码接口
     */
    @Operation(summary = "重置密码接口", description = "通过邮箱验证码重置用户密码")
    @PostMapping("/password/reset")
    public Result<Void> resetPassword(@RequestBody ResetPasswordDTO resetPasswordDTO) {
        userService.resetPassword(resetPasswordDTO);
        return Result.success();
    }


    /**
     * 发送验证码到邮箱方法
     *
     * @param email 用户邮箱
     * @param code  验证码
     * @return 发送结果
     */
    private @NonNull Result<Void> sendCode2Email(String email, String code, String messageType) {
        try {
            SimpleMailMessage message = new SimpleMailMessage();
            message.setFrom(mailProperties.getFrom());
            message.setTo(email);
            message.setSubject(mailProperties.getSubjectPrefix());
            message.setText(buildVerificationText(code, messageType));
            javaMailSender.send(message);
            //设置频率限制标记,1分钟内不可重复发送
            redisCacheUtil.set(rateLimitKey, "1", 1L, TimeUnit.MINUTES);
            return Result.success(200, AuthConstant.REAL_CODE_SEND_MESSAGE);
        } catch (Exception e) {
            log.error("邮件发送失败", e);
            return Result.error(AuthConstant.CODE_SEND_FAIL);
        }
    }

    private String buildVerificationText(String code, String messageType) {
        String warningText = switch (messageType) {
            case "注册" -> "如非本人操作，请忽略此邮件。";
            case "找回" -> "如非本人操作，请立即检查账号安全或修改密码。";
            default -> "如非本人操作，请忽略。";
        };

        return String.format("您的%s验证码是：%s，有效期%d分钟。%s", messageType,
                code, mailProperties.getCodeExpireMinutes(), warningText);
    }
}
