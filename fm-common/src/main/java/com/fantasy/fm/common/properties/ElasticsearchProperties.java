package com.fantasy.fm.common.properties;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Data
@Component
@ConfigurationProperties(prefix = "fm.elasticsearch")
public class ElasticsearchProperties {
    private String host;
    private String username;
    private String password;
}
