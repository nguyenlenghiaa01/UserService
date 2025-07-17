package com.example.UserService.InterFace;

import com.example.UserService.Enity.Account;
import com.example.UserService.Model.Request.*;
import com.example.UserService.Model.Response.AccountResponse;
import com.example.UserService.Model.Response.DataResponse;
import org.springframework.data.domain.Pageable;

public interface IAuthenticationService {
    AccountResponse register(RegisterRequest registerRequest);
    AccountResponse login(LoginRequest loginRequest);
    DataResponse<AccountResponse> getAllAccount(int page, int size);
    Account updateAccount(String id, AccountUpdateRequest registerRequest);
    Account deleteAccount(String id);
    void resetPassword(ResetPasswordRequest resetPasswordRequest);
    String changePassword(ChangePasswordRequest changePasswordRequest);
    DataResponse<AccountResponse> searchAccounts(String name, Pageable pageable);
    AccountResponse findAccountByEmail(String email);
    AccountResponse getUserByUuid(String uuid);
}
