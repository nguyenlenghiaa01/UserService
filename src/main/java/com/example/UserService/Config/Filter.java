
package com.example.UserService.Config;

import com.example.UserService.Enity.UserPrincipal;
import com.example.UserService.Exception.AuthException;
import com.example.UserService.Service.TokenService;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.MalformedJwtException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.util.AntPathMatcher;
import org.springframework.web.filter.OncePerRequestFilter;
import org.springframework.web.servlet.HandlerExceptionResolver;

import java.io.IOException;
import java.util.List;

@Component
@RequiredArgsConstructor
public class Filter extends OncePerRequestFilter {

    private final TokenService jwtUtil;
    private final HandlerExceptionResolver handlerExceptionResolver;

    private final List<String> PUBLIC_USER_SERVICE_APIS = List.of(
            "/api/users/login",
            "/api/users/register",
            "/api/users/forgot-password",
            "/api/users/getEmail",
            "/swagger-ui/**",
            "/v3/api-docs/**"
    );

    private final AntPathMatcher pathMatcher = new AntPathMatcher();

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {

        String requestURI = request.getRequestURI();

        boolean isPublicAPI = PUBLIC_USER_SERVICE_APIS.stream()
                .anyMatch(pattern -> pathMatcher.match(pattern, requestURI));

        if (isPublicAPI) {
            filterChain.doFilter(request, response);
            return;
        }

        String token = getTokenFromRequest(request);

        if (token == null) {
            handlerExceptionResolver.resolveException(request, response, null, new AuthException("JWT token is missing."));
            return;
        }

        UserPrincipal userPrincipal;
        try {
            userPrincipal = jwtUtil.parseToken(token);
            if (userPrincipal == null || userPrincipal.getUserId() == null || userPrincipal.getAuthorities().isEmpty()) {
                handlerExceptionResolver.resolveException(request, response, null, new AuthException("Invalid JWT claims or missing user information."));
                return;
            }

        } catch (ExpiredJwtException e) {
            handlerExceptionResolver.resolveException(request, response, null, new AuthException("JWT token has expired."));
            return;
        } catch (MalformedJwtException e) {
            handlerExceptionResolver.resolveException(request, response, null, new AuthException("Invalid JWT token format."));
            return;
        } catch (Exception e) { // Catch các lỗi khác trong quá trình xử lý token
            handlerExceptionResolver.resolveException(request, response, null, new AuthException("JWT token processing error: " + e.getMessage()));
            return;
        }

        if (SecurityContextHolder.getContext().getAuthentication() == null) {
            UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(
                    userPrincipal,
                    null,
                    userPrincipal.getAuthorities()
            );
            authenticationToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
            SecurityContextHolder.getContext().setAuthentication(authenticationToken);
        }

        filterChain.doFilter(request, response);
    }

    private String getTokenFromRequest(HttpServletRequest request) {
        String authHeader = request.getHeader("Authorization");
        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            return authHeader.substring(7);
        }
        return null;
    }
}