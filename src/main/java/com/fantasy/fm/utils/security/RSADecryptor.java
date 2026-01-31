package com.fantasy.fm.utils.security;

import com.fantasy.fm.properties.LoginProperties;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.Cipher;
import java.security.KeyFactory;
import java.security.PrivateKey;
import java.security.spec.PKCS8EncodedKeySpec;
import java.util.Base64;

@Component
@RequiredArgsConstructor
public class RSADecryptor {

    private final LoginProperties loginProperties;

    public String decrypt(String encryptedText) throws Exception {
        //如果没有开启加密，直接返回原文
        if (!loginProperties.getIsEnablePasswordEncrypt()) {
            return encryptedText;
        }

        byte[] keyBytes = Base64.getDecoder().decode(loginProperties.getPrivateKeyString());
        PKCS8EncodedKeySpec spec = new PKCS8EncodedKeySpec(keyBytes);
        KeyFactory kf = KeyFactory.getInstance("RSA");
        PrivateKey privateKey = kf.generatePrivate(spec);

        Cipher cipher = Cipher.getInstance("RSA");
        cipher.init(Cipher.DECRYPT_MODE, privateKey);

        // 前端 jsencrypt 默认输出 Base64 字符串，需要先解码
        byte[] encryptedBytes = Base64.getDecoder().decode(encryptedText);
        byte[] decryptedBytes = cipher.doFinal(encryptedBytes);

        return new String(decryptedBytes);
    }
}
