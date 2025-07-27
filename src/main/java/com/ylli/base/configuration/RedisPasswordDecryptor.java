package com.ylli.base.configuration;

import jakarta.annotation.PostConstruct;
import org.jasypt.encryption.StringEncryptor;
import org.springframework.beans.BeanWrapper;
import org.springframework.beans.BeanWrapperImpl;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.boot.autoconfigure.data.redis.RedisProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;
import org.springframework.util.StringUtils;

/**
 * @author ylli
 */
@Configuration
public class RedisPasswordDecryptor implements BeanPostProcessor {

    private final ObjectProvider<StringEncryptor> stringEncryptorProvider;
    private boolean isEncryptorAvailable = false;

    public RedisPasswordDecryptor(ObjectProvider<StringEncryptor> stringEncryptorProvider) {
        this.stringEncryptorProvider = stringEncryptorProvider;
    }

    @PostConstruct
    public void init() {
        // 检查是否配置了加密器
        isEncryptorAvailable = stringEncryptorProvider.getIfAvailable() != null;

        System.out.println("isEncryptorAvailable: " + isEncryptorAvailable);
    }

    @Override
    public Object postProcessAfterInitialization(Object bean, String beanName) {
        if (bean instanceof RedisProperties) {
            RedisProperties redisProperties = (RedisProperties) bean;
            String password = redisProperties.getPassword();

            // 检查密码是否为加密格式（ENC(...)）
            if (isEncryptorAvailable && StringUtils.hasText(password) && isEncrypted(password)) {
                try {
                    String decryptedPassword = decrypt(password);
                    // 使用 BeanWrapper 安全修改密码字段
                    BeanWrapper beanWrapper = new BeanWrapperImpl(redisProperties);
                    beanWrapper.setPropertyValue("password", decryptedPassword);
                } catch (Exception e) {
                    throw new IllegalStateException("Failed to decrypt Redis password", e);
                }
            }
        }
        return bean;
    }

    private boolean isEncrypted(String password) {
        return password.startsWith("ENC(") && password.endsWith(")");
    }

    private String decrypt(String encryptedPassword) {
        StringEncryptor stringEncryptor = stringEncryptorProvider.getIfAvailable();
        if (stringEncryptor == null) {
            throw new IllegalStateException("No StringEncryptor available for decrypting Redis password");
        }
        // 去掉 ENC() 包装
        String actualEncrypted = encryptedPassword.substring(4, encryptedPassword.length() - 1);
        return stringEncryptor.decrypt(actualEncrypted);
    }
}
