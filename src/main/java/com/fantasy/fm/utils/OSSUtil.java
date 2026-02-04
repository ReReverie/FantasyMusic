package com.fantasy.fm.utils;

import com.aliyun.oss.ClientException;
import com.aliyun.oss.OSS;
import com.aliyun.oss.OSSClientBuilder;
import com.aliyun.oss.OSSException;
import com.aliyun.oss.model.PutObjectRequest;
import com.aliyun.oss.model.PutObjectResult;
import com.fantasy.fm.properties.OssProperties;
import jakarta.annotation.PreDestroy;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.io.ByteArrayInputStream;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;

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
        PutObjectResult result = null;
        try {
            //将字节数组转换成输入流
            ByteArrayInputStream inputStream = new ByteArrayInputStream(data);
            // 创建PutObjectRequest对象。
            PutObjectRequest putObjectRequest = new PutObjectRequest(ossProperties.getBucketName(), objectName, inputStream);
            // 创建PutObject请求。
            result = client.putObject(putObjectRequest);
        } catch (OSSException oe) {
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

    //关闭客户端连接
    @PreDestroy //自动调用
    private void shutdown() {
        if (client != null) {
            client.shutdown();
            log.info("OSSClient连接已关闭");
        }
    }
}
