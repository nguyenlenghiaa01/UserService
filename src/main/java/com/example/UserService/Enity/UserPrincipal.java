package com.example.UserService.Enity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection; // <--- Quan trọng: là Collection
import java.util.List;
import java.util.stream.Collectors;

@Data
@NoArgsConstructor
@AllArgsConstructor // <--- Annotation này tạo constructor với TẤT CẢ các fields
public class UserPrincipal implements UserDetails {
    private String userId;
    private Collection<String> roles; // Đây là Collection<String>

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return null;
    }

    @Override
    public String getPassword() {
        return null;
    }

    @Override
    public String getUsername() {
        return null;
    }
    // ... các method của UserDetails ...
}