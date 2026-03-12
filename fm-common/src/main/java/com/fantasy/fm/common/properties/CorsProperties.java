package com.fantasy.fm.common.properties;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import java.util.List;

@Data
@Component
@ConfigurationProperties(prefix = "fm.cors")
public class CorsProperties {
    private List<String> origins;
    private List<String> methods;
    private String headers;
}
