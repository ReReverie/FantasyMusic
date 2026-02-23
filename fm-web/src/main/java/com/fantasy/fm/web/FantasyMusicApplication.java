package com.fantasy.fm.web;

import com.fantasy.fm.web.config.ServerConfig;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.Import;

@EnableCaching // 开启缓存功能
@SpringBootApplication
@MapperScan("com.fantasy.fm.service.mapper") // 扫描 Mapper 接口所在的包
@Import(ServerConfig.class) // 导入 ServerConfig 配置类
public class FantasyMusicApplication {

    public static void main(String[] args) {
        SpringApplication.run(FantasyMusicApplication.class, args);
    }

}
