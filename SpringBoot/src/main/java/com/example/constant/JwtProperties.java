package com.example.constant;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Component
@ConfigurationProperties(prefix = "jwt-properties")
@Data
public class JwtProperties {

    //存活时间单位是毫秒
    private Long ttlMillis;

    //密钥
    private String secretKey;

}
