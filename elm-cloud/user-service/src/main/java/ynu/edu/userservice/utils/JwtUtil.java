package ynu.edu.userservice.utils;

import io.jsonwebtoken.*;

import java.util.Date;
import java.util.Map;

public class JwtUtil {

    private static final String signKey = "elmcloud-secret-key";  // 签名密钥
    private static final Long expire = 86400000L;     // 过期时间（24小时）

    /**
     * 生成JWT令牌
     */
    public static String generateJwt(Map<String, Object> claims) {
        return Jwts.builder()
                .addClaims(claims)
                .signWith(SignatureAlgorithm.HS256, signKey)  // 使用HS256算法签名
                .setExpiration(new Date(System.currentTimeMillis() + expire))  // 设置过期时间
                .compact();
    }

    /**
     * 解析JWT令牌
     */
    public static Claims parseJWT(String jwt) {
        try {
            return Jwts.parser()
                    .setSigningKey(signKey)  // 设置签名密钥
                    .parseClaimsJws(jwt)     // 解析JWT
                    .getBody();              // 获取负载内容
        } catch (ExpiredJwtException e) {
            throw new RuntimeException("Token已过期", e);
        } catch (SignatureException e) {
            throw new RuntimeException("签名不匹配", e);
        } catch (MalformedJwtException e) {
            throw new RuntimeException("Token格式错误", e);
        } catch (Exception e) {
            throw new RuntimeException("无效的Token", e);
        }
    }
} 