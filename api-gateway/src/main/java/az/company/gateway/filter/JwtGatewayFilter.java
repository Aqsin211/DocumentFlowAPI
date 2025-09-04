package az.company.gateway.filter;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.nio.charset.StandardCharsets;
import java.security.Key;

@Component
public class JwtGatewayFilter implements GlobalFilter, Ordered {

    private final Key key;

    public JwtGatewayFilter(@Value("${jwt.secret-key}") String secret ){
        this.key = Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8));
    }

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, org.springframework.cloud.gateway.filter.GatewayFilterChain chain) {
        String path = exchange.getRequest().getURI().getPath();

        // Allow public endpoints
        if (isPublic(path, exchange.getRequest().getMethod())) {
            return chain.filter(exchange);
        }

        String auth = exchange.getRequest().getHeaders().getFirst(HttpHeaders.AUTHORIZATION);
        if (auth == null || !auth.startsWith("Bearer ")) {
            exchange.getResponse().setStatusCode(HttpStatus.UNAUTHORIZED);
            return exchange.getResponse().setComplete();
        }

        String token = auth.substring(7);
        Claims claims;
        try {
            claims = Jwts.parserBuilder()
                    .setSigningKey(key)
                    .build()
                    .parseClaimsJws(token)
                    .getBody();
        } catch (JwtException e) {
            exchange.getResponse().setStatusCode(HttpStatus.UNAUTHORIZED);
            return exchange.getResponse().setComplete();
        }

        String jti = claims.getId();
        if (jti == null) {
            exchange.getResponse().setStatusCode(HttpStatus.UNAUTHORIZED);
            return exchange.getResponse().setComplete();
        }

        ServerHttpRequest mutated = exchange.getRequest().mutate()
                .header("X-User-ID", String.valueOf(claims.get("userId")))
                .header("X-Username", claims.get("username", String.class))
                .header("X-Role", claims.get("role", String.class))
                .header("X-Internal-Gateway", "true")
                .build();

        return chain.filter(exchange.mutate().request(mutated).build());
    }

    private boolean isPublic(String path, HttpMethod method) {
        // Auth & user/admin creation
        if ((path.equals("/auth") && HttpMethod.POST.equals(method))
                || (path.equals("/user") && HttpMethod.POST.equals(method))
                || (path.equals("/admin") && HttpMethod.POST.equals(method))) return true;

        // Swagger UI & API docs
        if (path.startsWith("/swagger") || path.startsWith("/swagger-ui") || path.startsWith("/v3/api-docs")) return true;

        // Proxied microservices API docs
        return path.matches("^/(user|admin|auth|documents)/v3/api-docs.*");
    }

    @Override
    public int getOrder() {
        return -100; // high priority
    }
}
