package com.tradetrackpro.security;

import jakarta.servlet.*;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.stereotype.Component;
import java.io.IOException;

@Component
public class JwtFilter implements Filter {

    private final JwtUtil jwtUtil;

    public JwtFilter(JwtUtil jwtUtil) {
        this.jwtUtil = jwtUtil;
    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {

        HttpServletRequest req = (HttpServletRequest) request;
        String authHeader = req.getHeader("Authorization");

        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            String token = authHeader.substring(7);
            System.out.println("JwtFilter: Token received in header: " + token.substring(0, Math.min(10, token.length())) + "...");

            try {
                Long userId = jwtUtil.getUserId(token);
                System.out.println("JwtFilter: Validated token, extracted userId: " + userId);
                req.setAttribute("userId", userId);

            } catch (Exception e) {
                System.out.println("JwtFilter: Token validation FAILED: " + e.getMessage());
                e.printStackTrace();
            }
        } else {
            System.out.println("JwtFilter: No valid Authorization header found. Header value: " + authHeader);
        }

        chain.doFilter(request, response);
    }
}
