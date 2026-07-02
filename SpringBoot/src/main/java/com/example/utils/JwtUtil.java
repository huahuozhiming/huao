package com.example.utils;

import com.example.constant.Constant;
import com.example.constant.JwtProperties;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Date;

@Slf4j
@Component
public class JwtUtil {


    @Autowired
    //注入配置属性类
    private JwtProperties jwtProperties;

    //设置密钥
    private SecretKey getSecretKey() {

        //将配置类中的密钥转换成 UTF-8 编码的字节数组
        byte[] keyBytes = jwtProperties.getSecretKey().getBytes(StandardCharsets.UTF_8);
        //进行HMAC-SHA加密该方法自动选择合适的长度最少256bit（32B）
        return Keys.hmacShaKeyFor(keyBytes);
}

    //依据用户名生成token
    public String generateToken(String id){

        //1970.1.1 00:00到现在的毫秒差值
        long currentTimeMillis = System.currentTimeMillis();
        //这代码与Date now = new Date();等价方法内部会自动填入currentTimeMillis
        //这里并未简化是因为后面要用now
        Date now=new Date(currentTimeMillis);
        //依据ttl计算密钥过期时间
        Date expiration=new Date(currentTimeMillis+jwtProperties.getTtlMillis());

        return Jwts.builder()
                .setSubject(id)
                .setIssuedAt(now)
                .setExpiration(expiration)
                .signWith(getSecretKey())
                .compact();

    }

    //获取payload部分的声明
    public Claims getClaims(String token){
        return Jwts.parserBuilder()
                .setSigningKey(getSecretKey())
                .build()
                .parseClaimsJws(token)
                .getBody();
    }

}
