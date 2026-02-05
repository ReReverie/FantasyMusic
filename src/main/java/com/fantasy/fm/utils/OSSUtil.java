package com.fantasy.fm.utils;

import com.aliyun.oss.ClientException;
import com.aliyun.oss.OSS;
import com.aliyun.oss.OSSClientBuilder;
import com.aliyun.oss.OSSException;
import com.aliyun.oss.model.GeneratePresignedUrlRequest;
import com.aliyun.oss.model.PutObjectRequest;
import com.aliyun.oss.model.PutObjectResult;
import com.aliyun.oss.model.ResponseHeaderOverrides;
import com.fantasy.fm.properties.OssProperties;
import jakarta.annotation.PreDestroy;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.io.ByteArrayInputStream;
import java.net.URL;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.Date;

@Slf4j
@Component
public class OSSUtil {
    private final OssProperties ossProperties;
    //单例复用,避免每次上传都创建客户端
    private final OSS client;

    //自定义构造方法注入属性
    private OSSUtil(OssProperties properties) {
        this.ossProperties = properties;
        this.client = new OSSClientBuilder().build(
                properties.getEndpoint(),
                properties.getAccessKeyId(),
                properties.getAccessKeySecret()
        );
        log.info("OSSClient初始化完成: {}", ossProperties.getEndpoint());
    }

    /**
     * 上传文件到阿里云OSS
     *
     * @param data       文件字节数组
     * @param objectName 对象名称(包含路径)
     * @return 文件访问URL
     */
    public String upload(byte[] data, String objectName) {
        try {
            //将字节数组转换成输入流
            ByteArrayInputStream inputStream = new ByteArrayInputStream(data);
            // 创建PutObjectRequest对象。
            PutObjectRequest putObjectRequest = new PutObjectRequest(ossProperties.getBucketName(), objectName, inputStream);
            // 创建PutObject请求。
            client.putObject(putObjectRequest);
        } catch (OSSException oe) {
            log.error("文件上传到OSS时出现问题,{}", oe.getErrorMessage());
            System.out.println("Caught an OSSException, which means your request made it to OSS, "
                    + "but was rejected with an error response for some reason.");
            System.out.println("Error Message:" + oe.getErrorMessage());
            System.out.println("Error Code:" + oe.getErrorCode());
            System.out.println("Request ID:" + oe.getRequestId());
            System.out.println("Host ID:" + oe.getHostId());
        } catch (ClientException ce) {
            System.out.println("Caught an ClientException, which means the client encountered "
                    + "a serious internal problem while trying to communicate with OSS, "
                    + "such as not being able to access the network.");
            System.out.println("Error Message:" + ce.getMessage());
        } catch (Exception e) {
            log.error("上传文件到OSS失败: {}", e.getMessage());
            return null;
        }
        //文件访问路径规则:https://BucketName.Endpoint/ObjectName
        String url = "https://" + ossProperties.getBucketName()
                + "." + ossProperties.getEndpoint() + "/" + objectName;
        log.info("上传成功: {}", url);
        return url;
    }

    /**
     * 删除OSS对应的文件
     */
    public void delete(String objectName) {
        try {
            // 删除文件或目录。如果要删除目录，目录必须为空。
            client.deleteObject(ossProperties.getBucketName(), objectName);
        } catch (OSSException oe) {
            log.error("文件从OSS删除时出现问题,{}", oe.getErrorMessage());
            System.out.println("Caught an OSSException, which means your request made it to OSS, "
                    + "but was rejected with an error response for some reason.");
            System.out.println("Error Message:" + oe.getErrorMessage());
            System.out.println("Error Code:" + oe.getErrorCode());
            System.out.println("Request ID:" + oe.getRequestId());
            System.out.println("Host ID:" + oe.getHostId());
        } catch (ClientException ce) {
            System.out.println("Caught an ClientException, which means the client encountered "
                    + "a serious internal problem while trying to communicate with OSS, "
                    + "such as not being able to access the network.");
            System.out.println("Error Message:" + ce.getMessage());
        }
    }

    /**
     * 生成预签名URL
     */
    public URL generatePresignedUrl(String objectName) {
        try {
            // 设置预签名URL过期时间，单位为毫秒。本示例以设置过期时间为1小时为例。
            Date expiration = new Date(new Date().getTime() + ossProperties.getExpireTime() * 1000L);
            // 生成以GET方法访问的预签名URL。本示例没有额外请求头，其他人可以直接通过浏览器访问相关内容。
            return client.generatePresignedUrl(ossProperties.getBucketName(), objectName, expiration);
        } catch (OSSException oe) {
            log.error("生成预签名URL时出现问题,{}", oe.getErrorMessage());
            System.out.println("Caught an OSSException, which means your request made it to OSS, "
                    + "but was rejected with an error response for some reason.");
            System.out.println("Error Message:" + oe.getErrorMessage());
            System.out.println("Error Code:" + oe.getErrorCode());
            System.out.println("Request ID:" + oe.getRequestId());
            System.out.println("Host ID:" + oe.getHostId());
        } catch (ClientException ce) {
            System.out.println("Caught an ClientException, which means the client encountered "
                    + "a serious internal problem while trying to communicate with OSS, "
                    + "such as not being able to access the network.");
            System.out.println("Error Message:" + ce.getMessage());
        }
        return null;
    }

    /**
     * 生成预签名URL
     */
    public URL generateDownloadPresignedUrl(String objectName, String fileName) {
        try {
            // 1. 设置过期时间
            Date expiration = new Date(System.currentTimeMillis() + ossProperties.getExpireTime() * 1000L);
            // 2. 创建请求对象
            GeneratePresignedUrlRequest request = new GeneratePresignedUrlRequest(ossProperties.getBucketName(), objectName);
            request.setExpiration(expiration);
            // 3. 设置响应头 (告诉浏览器强制下载并指定文件名)
            ResponseHeaderOverrides headers = new ResponseHeaderOverrides();
            try {
                // 处理中文文件名
                String encodedName = URLEncoder.encode(fileName, StandardCharsets.UTF_8)
                        .replaceAll("\\+", "%20");
                headers.setContentDisposition("attachment; filename*=UTF-8''" + encodedName);
            } catch (Exception e) {
                // 兜底方案
                headers.setContentDisposition("attachment; filename=\"" + fileName
                        .replaceAll("[^a-zA-Z0-9._-]", "_") + "\"");
            }
            request.setResponseHeaders(headers);
            // 4. 生成 URL
            return client.generatePresignedUrl(request);
        } catch (OSSException oe) {
            log.error("生成预签名URL时出现问题,{}", oe.getErrorMessage());
            System.out.println("Caught an OSSException, which means your request made it to OSS, "
                    + "but was rejected with an error response for some reason.");
            System.out.println("Error Message:" + oe.getErrorMessage());
            System.out.println("Error Code:" + oe.getErrorCode());
            System.out.println("Request ID:" + oe.getRequestId());
            System.out.println("Host ID:" + oe.getHostId());
        } catch (ClientException ce) {
            System.out.println("Caught an ClientException, which means the client encountered "
                    + "a serious internal problem while trying to communicate with OSS, "
                    + "such as not being able to access the network.");
            System.out.println("Error Message:" + ce.getMessage());
        }
        return null;
    }

    //关闭客户端连接
    @PreDestroy //自动调用
    private void shutdown() {
        if (client != null) {
            client.shutdown();
            log.info("OSSClient连接已关闭");
        }
    }
}
