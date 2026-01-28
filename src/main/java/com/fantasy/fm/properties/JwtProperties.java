package com.fantasy.fm.properties;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Component
@ConfigurationProperties(prefix = "fm.jwt")
public class JwtProperties {
    private String secretKey;
    private Long expireTime;
    private String tokenName;
}
