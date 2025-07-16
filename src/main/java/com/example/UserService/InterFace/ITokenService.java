package com.example.UserService.InterFace;

import com.example.UserService.Model.Response.AuthorResponse;

import javax.crypto.SecretKey;

public interface ITokenService {
    SecretKey getSigninKey();
    AuthorResponse isAuthenticated(String token);
}
