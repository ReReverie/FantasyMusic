package com.fantasy.fm.properties;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Data
@Component
@ConfigurationProperties("fm.mail")
public class MailProperties {
    private String from;           // 发件人
    private String subjectPrefix;  // 主题前缀
    private Integer codeExpireMinutes;  // 验证码有效期
}
