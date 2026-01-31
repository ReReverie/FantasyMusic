package com.fantasy.fm.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenAPIConfig {

    @Bean
    public OpenAPI openAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("FantasyMusic API")
                        .description("幻想音乐API文档")
                        .version("v1.0.0")
                        .contact(new Contact()
                                .email("shidifuse@outlook.com")
                                .name("fantasy")));
    }
}
