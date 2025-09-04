package az.company.msauth.service;

import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.nio.charset.StandardCharsets;
import java.security.Key;
import java.util.Date;
import java.util.UUID;

@Component
public class JwtService  {

    private final Key key;
    private final long tokenValidityMillis;

    public JwtService(
            @Value("${jwt.secret-key}") String secret,
            @Value("${jwt.token-validity-ms:3600000}") long tokenValidityMillis) {
        this.key = Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8));
        this.tokenValidityMillis = tokenValidityMillis;
    }

    public String generateToken(Long userId, String username, String role) {
        Date now = new Date();
        Date exp = new Date(now.getTime() + tokenValidityMillis);
        String jti = UUID.randomUUID().toString();
        return Jwts.builder()
                .setSubject("DocumentFlowAPI")
                .setId(jti)
                .setIssuedAt(now)
                .setExpiration(exp)
                .claim("userId", userId)
                .claim("username", username)
                .claim("role", role)
                .signWith(key, SignatureAlgorithm.HS256)
                .compact();
    }

    public Claims parseClaims(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(key)
                .build()
                .parseClaimsJws(token)
                .getBody();
    }

}
