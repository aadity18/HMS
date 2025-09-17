package com.hms.gateway.filter;

import org.springframework.cloud.gateway.filter.GatewayFilter;
import org.springframework.cloud.gateway.filter.factory.AbstractGatewayFilterFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.util.AntPathMatcher;
import org.springframework.web.server.ServerWebExchange;

import com.hms.gateway.util.JwtUtil;

import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.web.server.ResponseStatusException;

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
            String path = exchange.getRequest().getURI().getPath();

            // 1.  completely open endpoints
            if (isOpen(path)) {
                return chain.filter(exchange);
            }

            // 2.  secured endpoints – token is mandatory
            String authHeader = exchange.getRequest().getHeaders().getFirst(HttpHeaders.AUTHORIZATION);
            if (authHeader == null || !authHeader.startsWith("Bearer ")) {
                throw new ResponseStatusException(HttpStatus.UNAUTHORIZED,
                        "Missing or invalid Authorization header");
            }

            String token = authHeader.substring(7);
            String role;
            String email;
            try {
                role   = jwtUtil.validateAndExtractRole(token);
                email  = jwtUtil.validateAndExtractEmail(token);
            } catch (Exception ex) {
                throw new ResponseStatusException(HttpStatus.UNAUTHORIZED,
                        "Invalid or expired token");
            }

            // 3.  role-based authorization
            if (!isAuthorized(path, exchange.getRequest().getMethod().name(), role)) {
                throw new ResponseStatusException(HttpStatus.FORBIDDEN,
                        "Insufficient permissions");
            }

            // 4.  forward user context to downstream services
            return chain.filter(
                    exchange.mutate()
                            .request(r -> r
                                    .header("X-User-Role",  role)
                                    .header("X-User-Email", email))
                            .build());
        };
    }

    /* ---------- helpers ---------- */

    private boolean isOpen(String path) {
        return pathMatcher.match("/auth/login", path);
    }

    private boolean isAuthorized(String path, String method, String role) {
        // OWNER-only endpoints
        if (pathMatcher.match("/auth/register", path)) {
            return "OWNER".equals(role);
        }

        // OWNER has full access
        if ("OWNER".equals(role)) {
            return true;
        }

        // MANAGER permissions
        if ("MANAGER".equals(role)) {
            switch (method) {
                case "GET":
                    return matchAny(path, "/api/rooms/**", "/api/reservations/**",
                                       "/api/staff/**", "/api/guests/**",
                                       "/api/bills/**", "/payment/**");
                case "POST":
                    return matchAny(path, "/api/rooms/**", "/api/reservations/**",
                                       "/api/guests/**", "/api/bills/**", "/payment/**");
                case "PUT":
                case "PATCH":
                    return matchAny(path, "/api/rooms/**", "/api/reservations/**",
                                       "/api/guests/**", "/api/bills/**", "/payment/**");
                case "DELETE":
                    return matchAny(path, "/api/reservations/**", "/api/guests/**");
            }
        }

        // RECEPTIONIST permissions
        if ("RECEPTIONIST".equals(role)) {
            switch (method) {
                case "GET":
                    return matchAny(path, "/api/rooms/**", "/api/reservations/**",
                                       "/api/guests/**", "/api/bills/**", "/payment/**");
                case "POST":
                    return matchAny(path, "/api/reservations/**", "/api/guests/**",
                                       "/api/bills/**", "/payment/**");
                case "PUT":
                case "PATCH":
                    return matchAny(path, "/api/reservations/**", "/api/guests/**");
            }
        }

        return false;
    }

    private boolean matchAny(String path, String... patterns) {
        for (String p : patterns) {
            if (pathMatcher.match(p, path)) return true;
        }
        return false;
    }

    public static class Config { }
}