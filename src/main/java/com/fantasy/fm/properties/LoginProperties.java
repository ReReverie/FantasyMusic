package com.fantasy.fm.properties;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Data
@Component
@ConfigurationProperties(prefix = "fm.login")
public class LoginProperties {
    private Boolean isEnablePasswordEncrypt;
    private String privateKeyString;
}
