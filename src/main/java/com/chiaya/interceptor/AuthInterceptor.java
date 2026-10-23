package com.chiaya.interceptor;

import com.chiaya.common.Result;
import com.chiaya.context.UserContext;
import com.chiaya.util.JwtUtil;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.util.AntPathMatcher;
import org.springframework.web.servlet.HandlerInterceptor;

import java.nio.charset.StandardCharsets;
import java.util.List;

/**
 * 登录校验拦截器：开放接口直接放行，其余 /api 接口必须携带
 * Authorization: Bearer &lt;token&gt;。
 */
@Component
public class AuthInterceptor implements HandlerInterceptor {

    /** 无需登录即可访问的接口（HTTP 方法 + 路径） */
    private static final List<String> PUBLIC_ENDPOINTS = List.of(
            "POST /api/users/register",
            "GET /api/users/admin-exists",
            "POST /api/users/register-admin",
            "POST /api/users/login",
            "GET /api/articles",
            "GET /api/articles/*",
            "GET /api/articles/*/comments",
            "GET /api/settings",
            "GET /api/blog/**",
            "GET /api/moments",
            "GET /api/guest-messages",
            "POST /api/guest-messages"
    );

    private static final AntPathMatcher PATH_MATCHER = new AntPathMatcher();

    private final JwtUtil jwtUtil;

    public AuthInterceptor(JwtUtil jwtUtil) {
        this.jwtUtil = jwtUtil;
    }

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler)
            throws Exception {
        if (HttpMethod.OPTIONS.matches(request.getMethod())) {
            return true;
        }
        Long userId = jwtUtil.parseUserId(resolveToken(request));
        if (isPublicEndpoint(request)) {
            // 公开接口：若携带了有效 Token，同样记录登录用户（例如已登录用户发表评论）
            if (userId != null) {
                UserContext.setUserId(userId);
            }
            return true;
        }
        if (userId == null) {
            writeUnauthorized(response);
            return false;
        }
        UserContext.setUserId(userId);
        return true;
    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) {
        UserContext.clear();
    }

    private boolean isPublicEndpoint(HttpServletRequest request) {
        String endpoint = request.getMethod() + " " + request.getRequestURI();
        return PUBLIC_ENDPOINTS.stream()
                .anyMatch(pattern -> PATH_MATCHER.match(pattern.split(" ", 2)[1], endpoint.split(" ", 2)[1])
                        && pattern.split(" ", 2)[0].equalsIgnoreCase(request.getMethod()));
    }

    private String resolveToken(HttpServletRequest request) {
        String authorization = request.getHeader("Authorization");
        if (authorization != null && authorization.startsWith("Bearer ")) {
            return authorization.substring("Bearer ".length()).trim();
        }
        return null;
    }

    private void writeUnauthorized(HttpServletResponse response) throws Exception {
        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        response.setCharacterEncoding(StandardCharsets.UTF_8.name());
        Result<Void> body = Result.error(401, "未登录或登录已过期");
        response.getWriter().write("{\"code\":" + body.getCode()
                + ",\"message\":\"" + body.getMessage() + "\",\"data\":null}");
    }
}
