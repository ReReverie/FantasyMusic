package com.fantasy.fm.web.config;

import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.ComponentScans;
import org.springframework.context.annotation.Configuration;

@Configuration
@ComponentScans({
        @ComponentScan("com.fantasy.fm.service.service"),
        @ComponentScan("com.fantasy.fm.common")
})
public class ServerConfig {
}
