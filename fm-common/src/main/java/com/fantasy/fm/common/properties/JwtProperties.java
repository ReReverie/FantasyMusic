package com.fantasy.fm.common.properties;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Data
@Component
@ConfigurationProperties(prefix = "fm.jwt")
public class JwtProperties {
    private String secretKey;
    private Long expireTime;
    private String tokenName;
}
