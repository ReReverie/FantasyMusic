package com.fantasy.fm.web.config;

import co.elastic.clients.elasticsearch.ElasticsearchClient;
import co.elastic.clients.elasticsearch.indices.CreateIndexResponse;
import com.fantasy.fm.common.properties.ElasticsearchProperties;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jspecify.annotations.NonNull;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Component;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;

@Configuration
@RequiredArgsConstructor
public class ElasticsearchConfig {

    private final ElasticsearchProperties elasticsearchProperties;

    @Bean(destroyMethod = "close") //确保在应用关闭时正确释放资源
    public ElasticsearchClient elasticsearchClient() {
        return ElasticsearchClient.of(
                builder -> builder.host(elasticsearchProperties.getHost())
                        .usernameAndPassword(elasticsearchProperties.getUsername(), elasticsearchProperties.getPassword())
        );
    }

}

@Slf4j
@Component
@RequiredArgsConstructor
class IndexInitializer implements ApplicationRunner {

    private final ElasticsearchClient elasticsearchClient;

    private static final String INDEX_NAME = "music_index";
    private static final String MAPPING_FILE_PATH = "database/ES_mappings_music_index.json";  // 使用 / 分隔

    @Override
    public void run(@NonNull ApplicationArguments args) throws Exception {
        createIndexIfNotExists();
    }

    private void createIndexIfNotExists() throws Exception {
        // 检查索引是否存在
        boolean indexExists = elasticsearchClient.indices()
                .exists(e -> e.index(INDEX_NAME))
                .value();

        if (indexExists) {
            log.info("ES索引 {} 已存在，跳过创建", INDEX_NAME);
            return;
        }

        log.info("ES索引 {} 不存在，开始创建...", INDEX_NAME);

        // 从 JSON 文件创建索引
        boolean created = createIndexFromJson();

        if (created) {
            log.info("ES索引 {} 创建成功", INDEX_NAME);
        } else {
            log.error("ES索引 {} 创建失败", INDEX_NAME);
            System.exit(1); // 退出应用，防止继续运行
        }
    }

    /**
     * 从 JSON 文件读取映射并创建索引
     */
    private boolean createIndexFromJson() {
        try {
            // 获取项目根目录的绝对路径
            String userDir = System.getProperty("user.dir");
            // 构建完整文件路径
            String fullPath = userDir + File.separator + MAPPING_FILE_PATH;
            File mappingFile = new File(fullPath);

            log.info("尝试读取文件: {}", mappingFile.getAbsolutePath());

            if (!mappingFile.exists()) {
                log.error("映射文件不存在: {}", mappingFile.getAbsolutePath());
                return false;
            }

            // 使用 FileInputStream 读取文件
            try (InputStream inputStream = new FileInputStream(mappingFile)) {
                // 创建索引，直接使用 InputStream
                CreateIndexResponse response = elasticsearchClient.indices()
                        .create(c -> c
                                .index(INDEX_NAME)
                                .withJson(inputStream)
                        );

                return response.acknowledged();
            }

        } catch (Exception e) {
            log.error("从文件创建索引失败", e);
            return false;
        }
    }
}