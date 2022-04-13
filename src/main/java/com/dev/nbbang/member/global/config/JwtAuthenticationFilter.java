package com.dev.nbbang.member.global.config;

import com.dev.nbbang.member.global.util.JwtUtil;
import com.dev.nbbang.member.global.util.RedisUtil;
import io.jsonwebtoken.ExpiredJwtException;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.GenericFilterBean;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@RequiredArgsConstructor
public class JwtAuthenticationFilter extends GenericFilterBean {

    private final JwtUtil jwtUtil;
    private final RedisUtil redisUtil;

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
        HttpServletRequest req = (HttpServletRequest)request;
        String token = req.getHeader("Authorization");
        String userId = null;
        String refreshToken = null;

        try {
            if(token != null && token.startsWith("Bearer ")) {
                token = token.substring(7);
                logger.info("토큰 값 존재, Bearer로시작 token >> " + token);
                userId = jwtUtil.getUserid(token);
                Authentication authentication = jwtUtil.getAuthentication(token);
                SecurityContextHolder.getContext().setAuthentication(authentication);
            }
        } catch(ExpiredJwtException e) {
            userId = e.getClaims().get("userId", String.class);
            refreshToken = redisUtil.getData(userId);
        } catch (Exception e) {
            e.printStackTrace();
        }

        try {
            if (refreshToken != null) {
                if (userId.equals(jwtUtil.getUserid(refreshToken))) {
                    String newToken = jwtUtil.generateAccessToken(userId, jwtUtil.getNickname(refreshToken));
                    Authentication authentication = jwtUtil.getAuthentication(newToken);
                    SecurityContextHolder.getContext().setAuthentication(authentication);
                    HttpServletResponse res = (HttpServletResponse) response;
                    res.addHeader("Authorization", "Bearer " + newToken);
                }
            }
        } catch(ExpiredJwtException e) {
        }
        chain.doFilter(request, response);
    }
}
