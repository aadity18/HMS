package com.example.api_gateway.filter;

import com.example.api_gateway.util.JwtUtil;
import org.springframework.cloud.gateway.filter.GatewayFilter;
import org.springframework.cloud.gateway.filter.factory.AbstractGatewayFilterFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Component;
import org.springframework.util.AntPathMatcher;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;

@Component
public class AuthenticationFilter extends AbstractGatewayFilterFactory<AuthenticationFilter.Config> {
    private final JwtUtil jwtUtil;
    private final AntPathMatcher pathMatcher = new AntPathMatcher();
    
    public AuthenticationFilter(JwtUtil jwtUtil) {
        super(Config.class);
        this.jwtUtil = jwtUtil;
    }

    @Override
    public GatewayFilter apply(Config config) {
        return (ServerWebExchange exchange, GatewayFilterChain chain) -> {
            // Skip validation for open routes
            if (isSecured(exchange)) {
                String authHeader = exchange.getRequest().getHeaders().getFirst(HttpHeaders.AUTHORIZATION);
                if (authHeader == null || !authHeader.startsWith("Bearer ")) {
                    throw new RuntimeException("Missing or invalid Authorization header");
                }

                String token = authHeader.substring(7);
         
                try {
                    String role = jwtUtil.validateAndExtractRole(token);
                    String method = exchange.getRequest().getMethod().toString();
                    String path = exchange.getRequest().getURI().getPath();
                    
                    // Direct path matcher-based authorization
                    if (!isAuthorized(method, path, role)) {
                        throw new RuntimeException("Unauthorized access: Insufficient permissions");
                    }
                    
                    return chain.filter(exchange.mutate()
                            .request(exchange.getRequest().mutate()
                                    .header("X-User-Role", role)
                                    .build())
                            .build());
                    
                } catch (Exception e) {
                    throw new RuntimeException("Unauthorized access: Invalid token");
                }
            }
            
            return chain.filter(exchange);
        };
    }

    private boolean isSecured(ServerWebExchange exchange) {
        String path = exchange.getRequest().getURI().getPath();
        return !path.startsWith("/auth/");
    }

    private boolean isAuthorized(String method, String path, String role) {
        // Direct path matching with role verification
        
        // OWNER has access to everything
        if ("OWNER".equals(role)) {
            return true;
        }
        
        // MANAGER permissions
        if ("MANAGER".equals(role)) {
            // GET access
            if ("GET".equals(method)) {
                return pathMatcher.match("/api/rooms/**", path) ||
                       pathMatcher.match("/api/reservations/**", path) ||
                       pathMatcher.match("/api/staff/**", path) ||
                       pathMatcher.match("/api/guests/**", path) ||
                       pathMatcher.match("/api/bills/**", path) ||
                       pathMatcher.match("/payment/**", path);
            }
            // POST access
            if ("POST".equals(method)) {
                return pathMatcher.match("/api/rooms/**", path) ||
                       pathMatcher.match("/api/reservations/**", path) ||
                       pathMatcher.match("/api/guests/**", path) ||
                       pathMatcher.match("/api/bills/**", path) ||
                       pathMatcher.match("/payment/**", path);
            }
            // PUT/PATCH access
            if ("PUT".equals(method) || "PATCH".equals(method)) {
                return pathMatcher.match("/api/rooms/**", path) ||
                       pathMatcher.match("/api/reservations/**", path) ||
                       pathMatcher.match("/api/guests/**", path) ||
                       pathMatcher.match("/api/bills/**", path) ||
                       pathMatcher.match("/payment/**", path);
            }
            // DELETE access
            if ("DELETE".equals(method)) {
                return pathMatcher.match("/api/reservations/**", path) ||
                       pathMatcher.match("/api/guests/**", path);
            }
        }
        
        // RECEPTIONIST permissions
        if ("RECEPTIONIST".equals(role)) {
            // GET access
            if ("GET".equals(method)) {
                return pathMatcher.match("/api/rooms/**", path) ||
                       pathMatcher.match("/api/reservations/**", path) ||
                       pathMatcher.match("/api/guests/**", path) ||
                       pathMatcher.match("/api/bills/**", path) ||
                       pathMatcher.match("/payment/**", path);
            }
            // POST access
            if ("POST".equals(method)) {
                return pathMatcher.match("/api/reservations/**", path) ||
                       pathMatcher.match("/api/guests/**", path) ||
                       pathMatcher.match("/api/bills/**", path) ||
                       pathMatcher.match("/payment/**", path);
            }
            // PUT/PATCH access
            if ("PUT".equals(method) || "PATCH".equals(method)) {
                return pathMatcher.match("/api/reservations/**", path) ||
                       pathMatcher.match("/api/guests/**", path);
            }
        }
        
        return false; 
    }

    public static class Config {
        
    }
}