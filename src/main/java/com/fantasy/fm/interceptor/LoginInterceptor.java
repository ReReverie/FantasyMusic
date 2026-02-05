package com.fantasy.fm.interceptor;

import cn.hutool.core.util.StrUtil;
import com.fantasy.fm.constant.AuthConstant;
import com.fantasy.fm.context.BaseContext;
import com.fantasy.fm.properties.JwtProperties;
import com.fantasy.fm.utils.JwtUtil;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.security.SignatureException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jspecify.annotations.Nullable;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

/**
 * 登录拦截器
 */
@Component
@Slf4j
@RequiredArgsConstructor
public class LoginInterceptor implements HandlerInterceptor {

    private final JwtProperties jwtProperties;

    /**
     * 预处理回调方法，实现处理器的预处理（如登录检查）。
     * 返回true继续流程（如调用下一个拦截器或处理器），
     * 返回false中断流程（如通过响应直接返回）。
     */
    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        String token = request.getHeader(jwtProperties.getTokenName());
        log.info("登录拦截器，获取到的令牌: {}", token);

        // 1. 令牌为空或空白
        if (StrUtil.isBlank(token)) {
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            return false;
        }

        // 2. 解析 JWT
        Claims claims;
        try {
            claims = JwtUtil.parseToken(jwtProperties.getSecretKey(), token);
        } catch (ExpiredJwtException e) {
            log.warn("JWT 已过期: {}", e.getMessage());
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            return false;
        } catch (SignatureException e) {
            log.warn("JWT 签名验证失败: {}", e.getMessage());
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            return false;
        } catch (MalformedJwtException e) {
            log.warn("JWT 格式错误: {}", e.getMessage());
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            return false;
        } catch (Exception e) {
            log.error("JWT 解析未知异常", e);
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            return false;
        }

        // 3. 获取用户ID（防御式编程）
        Object userIdObj = claims.get(AuthConstant.USER_ID);
        if (userIdObj == null) {
            log.warn("JWT 中未包含用户ID");
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            return false;
        }

        Long userId;
        try {
            userId = Long.valueOf(userIdObj.toString());
        } catch (NumberFormatException e) {
            log.warn("用户ID格式错误: {}", userIdObj);
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            return false;
        }

        log.info("当前用户id: {}", userId);
        BaseContext.setCurrentId(userId);
        return true;
    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, @Nullable Exception ex) throws Exception {
        //请求完一次移除线程中的数据,防止内存泄漏
        BaseContext.removeCurrentId();
    }
}
