package com.example.UserService.Service;

import com.example.UserService.Enity.Account;
import com.example.UserService.Repository.AccountRepository;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.util.Date;

@Service
public class TokenService {
    @Autowired
    private AccountRepository accountRepository;
    private String SECRET_KEY = "4bb6d1dfbafb64a681139d1586b6f1160d18159afd57c8c79136d7490630407c";

    private SecretKey getSigninKey() {
        byte[] keyBytes = Decoders.BASE64.decode(SECRET_KEY);
        return Keys.hmacShaKeyFor(keyBytes);
    }

    public String generateToken(Account account) {
        String token = Jwts.builder()
                .subject(account.getUuid() + "")
                .issuedAt(new Date(System.currentTimeMillis()))
                .expiration(new Date(System.currentTimeMillis() + 10008 * 60 * 60 * 24))//1 day
                .signWith(getSigninKey())
                .compact();
        return token;
    }

    // verify token
    public Account getAccountByToken(String token) {
        Claims claims = Jwts.parser()
                .verifyWith(getSigninKey())
                .build()
                .parseSignedClaims(token)
                .getPayload();

        String idString = claims.getSubject();
        return accountRepository.findAccountByUuid(idString);
    }
}