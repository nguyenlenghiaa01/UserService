package com.example.UserService.Service;

import com.example.UserService.Enity.UserPrincipal;
import com.example.UserService.Repository.AccountRepository;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.util.Arrays;
import java.util.Collection;
import java.util.stream.Collectors;

@Service
public class TokenService {

    @Autowired
    private AccountRepository accountRepository;

    private final String SECRET_KEY = "4bb6d1dfbafb64a681139d1586b6f1160d18159afd57c8c79136d7490630407c";

    private SecretKey getSigningKey() {
        byte[] keyBytes = Decoders.BASE64.decode(SECRET_KEY);
        return Keys.hmacShaKeyFor(keyBytes);
    }

    public UserPrincipal parseToken(String token) {
        Claims claims = Jwts.parserBuilder()
                .setSigningKey(getSigningKey())
                .build()
                .parseClaimsJws(token)
                .getBody();

        // Lấy userId từ 'sub'
        String userId = claims.getSubject();
        // Lấy vai trò từ 'roles'
        String rolesString = (String) claims.get("roles");

        // Chuyển đổi rolesString thành Collection<String>
        Collection<String> roles = Arrays.stream(rolesString.split(","))
                .map(String::trim)
                .collect(Collectors.toList());

        return new UserPrincipal(userId, roles);
    }

    public boolean validateToken(String token) {
        try {
            Jwts.parserBuilder().setSigningKey(getSigningKey()).build().parseClaimsJws(token);
            return true;
        } catch (Exception e) {
            // Log lỗi token không hợp lệ (ví dụ: hết hạn, sai chữ ký, v.v.)
            System.err.println("Token validation failed: " + e.getMessage());
            return false;
        }
    }
}
