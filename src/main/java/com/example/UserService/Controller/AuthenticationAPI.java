package com.example.UserService.Controller;

import com.example.UserService.Enity.Account;
import com.example.UserService.Model.Request.*;
import com.example.UserService.Model.Response.AccountResponse;
import com.example.UserService.Model.Response.DataResponse;
import com.example.UserService.Service.AuthenticationService;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.persistence.EntityNotFoundException;
import jakarta.validation.Valid;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;


@RestController
@RequestMapping("/api/users")
@SecurityRequirement(name = "api")
@CrossOrigin("*")
public class AuthenticationAPI {

    ModelMapper modelMapper = new ModelMapper();
    @Autowired
    AuthenticationService authenticationService;

    @PostMapping("register")
    public ResponseEntity <AccountResponse>register(@Valid @RequestBody RegisterRequest registerRequest) {
        AccountResponse newAccount = authenticationService.register(registerRequest);
        return ResponseEntity.status(HttpStatus.CREATED).body(newAccount);
    }

    @PostMapping("login")
    public ResponseEntity <AccountResponse> login(@Valid @RequestBody LoginRequest loginRequest) {
        AccountResponse accountResponse = authenticationService.login(loginRequest);
        if (accountResponse == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
        return ResponseEntity.ok(accountResponse);
    }

    @PreAuthorize("hasAuthority('ADMIN')")
    @GetMapping("account")
    public ResponseEntity <DataResponse<AccountResponse>>getAllAccount(@RequestParam int page,
                                                                       @RequestParam int size){
        DataResponse<AccountResponse> accounts = authenticationService.getAllAccount(page, size);
        return ResponseEntity.ok(accounts);
    }

    @PutMapping("/account/{id}")
    public ResponseEntity<AccountResponse> updateAccount(@PathVariable String id, @RequestBody AccountUpdateRequest registerRequest) {
        try {
            Account updatedAccount = authenticationService.updateAccount(id, registerRequest);
            AccountResponse accountResponse = modelMapper.map(updatedAccount, AccountResponse.class);
            return ResponseEntity.ok(accountResponse);
        } catch (EntityNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(null);
        }
    }

    @PreAuthorize("hasAuthority('ADMIN')")
    @DeleteMapping("{id}")
    public ResponseEntity<?> delete(@PathVariable String id){
        Account newAccount = authenticationService.deleteAccount(id);
        return ResponseEntity.ok(newAccount);
    }

//    @PostMapping("/loginGoogle")
//    public ResponseEntity<String> login(@RequestBody String idToken) {
//        try {
//            FirebaseToken decodedToken = FirebaseAuth.getInstance().verifyIdToken(idToken);
//            String uid = decodedToken.getUid();
//            return ResponseEntity.ok("User ID: " + uid);
//        } catch (FirebaseAuthException e) {
//            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Error: " + e.getMessage());
//        }
//    }

    @PostMapping("forgot-password")
    public ResponseEntity<?> forgotPassword(@Valid @RequestBody ForgotPasswordRequest forgotPasswordRequest){
        authenticationService.forgotPassword(forgotPasswordRequest);
        return ResponseEntity.ok("forgot password successfully");
    }

    @PostMapping("reset-password")
    public ResponseEntity<?> resetPassword(@Valid @RequestBody ResetPasswordRequest resetPasswordRequest){
        authenticationService.resetPassword(resetPasswordRequest);
        return ResponseEntity.ok("reset password successfully");
    }

    @PostMapping("change-password")
    public ResponseEntity<?> changePassword(@Valid @RequestBody ChangePasswordRequest changePasswordRequest) {
        String message = authenticationService.changePassword(changePasswordRequest);
        return ResponseEntity.ok(message);
    }

    @PreAuthorize("hasAuthority('ADMIN')")
    @GetMapping("/accounts/search")
    public ResponseEntity<DataResponse<AccountResponse>> searchAccounts(
            @RequestParam String name,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {

        Pageable pageable = PageRequest.of(page, size);
        DataResponse<AccountResponse> accounts = authenticationService.searchAccounts(name, pageable);
        return ResponseEntity.ok(accounts);
    }

}

