package com.example.UserService.Config;

import com.example.UserService.Exception.AuthException;
import com.example.UserService.Model.Response.AuthorResponse;
import com.example.UserService.Service.TokenService;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.MalformedJwtException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.util.AntPathMatcher;
import org.springframework.web.filter.OncePerRequestFilter;
import org.springframework.web.servlet.HandlerExceptionResolver;

import java.io.IOException;
import java.util.Collections;
import java.util.List;

@Component
public class Filter extends OncePerRequestFilter {


    @Autowired
    private TokenService tokenService;

    @Autowired
    @Qualifier("handlerExceptionResolver")
    HandlerExceptionResolver resolver;

    private final List<String> AUTH_PERMISSION = List.of(
            "/api/users/login",
            "/api/users/register",
            "/api/users/forgot-password",
            "/api/users/getEmail",
            "/swagger-ui/**",
            "/v3/api-docs/**",
            "/swagger-resources/**"
    );

    public boolean checkIsPublicAPI(String uri) {
        // uri: /api/register
        // nếu gặp những cái api trong list ở trên => cho phép truy cập lun => true
        AntPathMatcher patchMatch = new AntPathMatcher();
        // check token => false
        return AUTH_PERMISSION.stream().anyMatch(pattern -> patchMatch.match(pattern, uri));
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {

        // check xem cái api mà người dùng yêu cầu có phải là 1 public api?

        boolean isPublicAPI = checkIsPublicAPI(request.getRequestURI());

        if (isPublicAPI) {
            filterChain.doFilter(request, response);
        } else {
            String token = getTokenFromRequest(request);
            if (token == null) {
                // ko được phép truy cập
                resolver.resolveException(request, response, null, new AuthException("Empty token!"));
                return;
            }

            // => có token
            // check xem token có đúng hay ko => lấy thông tin account từ token
            AuthorResponse authorResponse;
            try {
                authorResponse = tokenService.isAuthenticated(token);
            } catch (ExpiredJwtException e) {
                // response token hết hạn
                resolver.resolveException(request, response, null, new AuthException("Expired token!"));
                return;
            } catch (MalformedJwtException malformedJwtException) {
                // response token sai
                resolver.resolveException(request, response, null, new AuthException("Invalid token!"));
                return;
            }
            if(authorResponse.getEmail() == null || authorResponse.getEmail().isEmpty() || authorResponse.getRole() == null || authorResponse.getRole().isEmpty()) {
                throw new RuntimeException("Error: Không thể decode token!");
            }
            // => token chuẩn
            // => cho phép truy cập
            // => lưu lại thông tin account
            UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(
                    authorResponse.getEmail(),
                    token,
                    Collections.singleton(new SimpleGrantedAuthority(authorResponse.getRole()))
            );
            authenticationToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
            SecurityContextHolder.getContext().setAuthentication(authenticationToken);
            // token ok, cho vao`
            filterChain.doFilter(request, response);
        }

    }

    private String getTokenFromRequest(HttpServletRequest request) {
        String authHeader = request.getHeader("Authorization");
        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            return authHeader.substring(7);
        }
        return null;
    }
}
