package com.example.UserService.Service;

import com.example.UserService.Enity.Account;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import com.example.UserService.Exception.AuthException;
import com.example.UserService.Exception.DuplicateEntity;
import com.example.UserService.Model.EmailDetail;
import com.example.UserService.Model.Request.*;
import com.example.UserService.Model.Response.AccountResponse;
import com.example.UserService.Model.Response.DataResponse;
import com.example.UserService.Repository.AccountRepository;
import jakarta.persistence.EntityNotFoundException;
import com.example.UserService.Exception.NotFoundException;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.data.domain.PageRequest;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.*;

@Service
@Lazy
public class AuthenticationService implements UserDetailsService {

    @Autowired
    private AccountRepository accountRepository;

    private ModelMapper modelMapper = new ModelMapper();

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private TokenService tokenService;

    @Autowired
    private EmailService emailService;


    public AccountResponse register(RegisterRequest registerRequest) {
        Account account = modelMapper.map(registerRequest, Account.class);
        try {
            String originPassword = account.getPassword();
            account.setPassword(passwordEncoder.encode(originPassword));
            String getAccountUuid;
            do {
                getAccountUuid =UUID.randomUUID().toString();
            } while (accountRepository.findAccountByUuid(getAccountUuid) != null);
            account.setUuid(getAccountUuid);
            account.setImage("");
            Account newAccount = accountRepository.save(account);
////            String otp = otpService.generateOtp();
////            emailService.sendOtp(account.getEmail(), otp);
////            // gui mail
//            EmailDetail emailDetail = new EmailDetail();
//            emailDetail.setReceiver(newAccount);
//            emailDetail.setSubject("WelCome");
//            emailDetail.setLink("abcd");
//            emailService.sendEmail(emailDetail);
            return modelMapper.map(newAccount, AccountResponse.class);
        } catch (Exception e) {
            if (e.getMessage().contains(account.getUsername())) {
                throw new DuplicateEntity("Duplicate username!");
            } else if (e.getMessage().contains(account.getEmail())) {
                throw new DuplicateEntity("Duplicate email!");
            } else if (e.getMessage().contains(account.getPhone())) {
                throw new DuplicateEntity("Duplicate phone!");
            } else {
                System.out.println(e.getMessage());
                throw e;
            }
        }
    }

    @Autowired
    AuthenticationManager authenticationManager;

    public AccountResponse login(LoginRequest loginRequest) {
        try {
            Authentication authentication = authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(
                    loginRequest.getUserName(),
                    loginRequest.getPassword()
            ));
            // tai khoan co ton tai
            Account account = (Account) authentication.getPrincipal();
            AccountResponse accountResponse = modelMapper.map(account, AccountResponse.class);
            accountResponse.setToken(tokenService.generateToken(account));
            return accountResponse;
        } catch (Exception e) {
            throw new EntityNotFoundException("User name or password is invalid !");
        }
    }

    public DataResponse<AccountResponse> getAllAccount(@RequestParam int page, @RequestParam int size) {
        Page accountPage = accountRepository.findAll(PageRequest.of(page, size));
        List<Account> accounts = accountPage.getContent();
        List<AccountResponse> accountResponses = new ArrayList<>();
        for(Account account: accounts) {
                AccountResponse accountResponse = new AccountResponse();
                accountResponse.setUuid(account.getUuid());
                accountResponse.setUserName(account.getUsername());
                accountResponse.setAddress(account.getAddress());
                accountResponse.setPhone(account.getPhone());
                accountResponse.setFullName(account.getFullName());
                accountResponse.setEmail(account.getEmail());
                accountResponse.setRole(account.getRole());
                accountResponse.setImage(account.getImage());

                accountResponses.add(accountResponse);
        }

        DataResponse<AccountResponse> dataResponse = new DataResponse<AccountResponse>();
        dataResponse.setListData(accountResponses);
        dataResponse.setTotalElements(accountPage.getTotalElements());
        dataResponse.setPageNumber(accountPage.getNumber());
        dataResponse.setTotalPages(accountPage.getTotalPages());
        return dataResponse;
    }


    @Override
    public UserDetails loadUserByUsername(String userName) throws UsernameNotFoundException {
        Account account = accountRepository.findAccountByUserName(userName);
        if (account == null) {
            throw new UsernameNotFoundException("Account with username " + userName + " not found");
        }
        return account;
    }

    public Account deleteAccount(String id){
        Account oldAccount = accountRepository.findAccountByUuid(id);
        if(oldAccount ==null){
            throw new NotFoundException("Account not found !");
        }
        accountRepository.delete(oldAccount);
        return oldAccount ;
    }

    public Account updateAccount(String id, AccountUpdateRequest newAccount) {
        Account oldAccount = accountRepository.findAccountByUuid(id);
        if(oldAccount ==null){
            throw new NotFoundException("Account not found !");
        }

        oldAccount.setEmail(newAccount.getEmail());
        oldAccount.setPhone(newAccount.getPhone());
        oldAccount.setFullName(newAccount.getFullName());
        oldAccount.setAddress(newAccount.getAddress());
        oldAccount.setImage(newAccount.getImage());

        return accountRepository.save(oldAccount);
    }

    public Account getCurrentAccount(){
        Account account = (Account) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        return accountRepository.findAccountByUuid(account.getUuid());
    }

    public void forgotPassword(ForgotPasswordRequest forgotPasswordRequest){
        Account account = accountRepository.findAccountByEmail(forgotPasswordRequest.getEmail());
        if(account == null){
            throw new NotFoundException("Email not found !");
        }else{
            EmailDetail emailDetail = new EmailDetail();
            emailDetail.setReceiver(account);
            emailDetail.setSubject("Reset Password");
            emailDetail.setLink("https://localhost:8082/reset-password?token="+tokenService.generateToken(account));
            emailService.sendEmail(emailDetail);
        }
    }

    public void resetPassword(ResetPasswordRequest resetPasswordRequest){
        Account account = getCurrentAccount();
        account.setPassword(passwordEncoder.encode(resetPasswordRequest.getPassword()));
        accountRepository.save(account);

    }

    public String changePassword(ChangePasswordRequest changePasswordRequest) {
        Account account = getCurrentAccount();
        String originPassword = account.getPassword();
        String message;

        try {
            if (!passwordEncoder.matches(changePasswordRequest.getCurrentPassword(), originPassword)) {
                message = "Invalid current password!";
                return message;
            }

            account.setPassword(passwordEncoder.encode(changePasswordRequest.getNewPassword()));
            accountRepository.save(account);
            message = "Successfully change your password!";

            return message;
        }catch (Exception e) {
            throw new AuthException(e.getMessage());
        }
    }

//    public UserResponse loginGoogle(String token){
//        try {
//            FirebaseToken decodedToken = FirebaseAuth.getInstance().verifyIdToken(token);
//            String email = decodedToken.getEmail();
//            Account account = accountRepository.findAccountByEmail(email);
//            if(account == null) {
//                Account newAccount = new Account();
//                newAccount.setUserName(decodedToken.getEmail());
//                newAccount.setEmail(decodedToken.getEmail());
//                newAccount.setFullName(decodedToken.getName());
//                newAccount.setImage(decodedToken.getPicture());
//                newAccount.setRole(Role.CONSULTANT);
//                account=accountRepository.save(newAccount);
//            }
//            UserResponse userResponse = new UserResponse();
//            userResponse.setToken(token);
//            userResponse.setFullName(account.getFullName());
//            userResponse.setImage(account.getImage());
//            userResponse.setRole(account.getRole());
//
//            return userResponse;
//
//        } catch (Exception e) {
//            throw new RuntimeException("Google token verification failed", e);
//        }
//
//    }

    public DataResponse<AccountResponse> searchAccounts(String name, Pageable pageable) {
        Page<Account> accounts = accountRepository.searchAccount(name, pageable);
        List<Account> accountss = accounts.getContent();
        List<AccountResponse> accountResponses = new ArrayList<>();

        for(Account account : accountss) {
            AccountResponse accountResponse = new AccountResponse();
            accountResponse.setUuid(account.getUuid());
            accountResponse.setUserName(account.getUsername());
            accountResponse.setAddress(account.getAddress());
            accountResponse.setPhone(account.getPhone());
            accountResponse.setFullName(account.getFullName());
            accountResponse.setEmail(account.getEmail());
            accountResponse.setRole(account.getRole());
            accountResponse.setImage(account.getImage());

            accountResponses.add(accountResponse);
        }

        DataResponse<AccountResponse> dataResponse = new DataResponse<>();
        dataResponse.setListData(accountResponses);
        dataResponse.setTotalElements(accounts.getTotalElements());
        dataResponse.setPageNumber(accounts.getNumber());
        dataResponse.setTotalPages(accounts.getTotalPages());

        return dataResponse;
    }

}
