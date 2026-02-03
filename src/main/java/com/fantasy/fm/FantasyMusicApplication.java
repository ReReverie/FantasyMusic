package com.fantasy.fm;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;

@EnableCaching // 开启缓存功能
@SpringBootApplication
public class FantasyMusicApplication {

    public static void main(String[] args) {
        SpringApplication.run(FantasyMusicApplication.class, args);
    }

}
