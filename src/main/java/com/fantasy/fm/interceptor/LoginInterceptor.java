package com.fantasy.fm.interceptor;

import cn.hutool.core.util.StrUtil;
import com.fantasy.fm.constant.LoginConstant;
import com.fantasy.fm.context.BaseContext;
import com.fantasy.fm.properties.JwtProperties;
import com.fantasy.fm.utils.JwtUtil;
import io.jsonwebtoken.Claims;
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
        //从请求头中获取令牌
        String token = request.getHeader(jwtProperties.getTokenName());

        //校验令牌
        log.info("登录拦截器，获取到的令牌:{}", token);
        //健壮性校验
        if (StrUtil.isNotBlank(token)) {
            Claims claims;
            try {
                claims = JwtUtil.parseToken(jwtProperties.getSecretKey(), token);
            } catch (Exception e) {
                //响应401状态码
                response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                return false;
            }
            Long userId = Long.valueOf(claims.get(LoginConstant.USER_ID).toString());
            log.info("当前用户id：{}", userId);
            //将当前用户id存入线程变量
            BaseContext.setCurrentId(userId);
            return true;
        } else {
            //响应401状态码
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            return false;
        }
    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, @Nullable Exception ex) throws Exception {
        //请求完一次移除线程中的数据,防止内存泄漏
        BaseContext.removeCurrentId();
    }
}
