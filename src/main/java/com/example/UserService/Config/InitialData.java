package com.example.UserService.Config;

import com.example.UserService.Enity.Account;
import com.example.UserService.Enum.Role;
import com.example.UserService.Repository.AccountRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.time.LocalDateTime;
import java.util.UUID;


@Configuration
@RequiredArgsConstructor
public class InitialData {
    private final PasswordEncoder passwordEncoder;
    @Bean
    public CommandLineRunner commandLineRunner(AccountRepository accountRepository) {
        return args -> {
            // Initialize the application repository with some data
            if(accountRepository.findAll().isEmpty()) {
                Account admin = Account.builder()
                        .uuid(String.valueOf(UUID.randomUUID()))
                        .userName("admin")
                        .password(passwordEncoder.encode("123456"))
                        .role(Role.ADMIN)
                        .createdAt(LocalDateTime.now())
                        .email("nghialncse170125@fpt.edu.vn")
                        .fullName("Admin user")
                        .phone("0987654321")
                        .address("FPT Admission System")
                        .image("")
                        .build();
                accountRepository.save(admin);

                Account consultant = Account.builder()
                        .uuid(String.valueOf(UUID.randomUUID()))
                        .userName("consultant")
                        .password(passwordEncoder.encode("123456"))
                        .role(Role.CONSULTANT)
                        .email("sonnqse182727@fpt.edu.vn")
                        .createdAt(LocalDateTime.now())
                        .fullName("Consultant user")
                        .phone("0987654322")
                        .address("FPT Admission System")
                        .image("")
                        .build();
                accountRepository.save(consultant);

                Account student = Account.builder()
                        .uuid(String.valueOf(UUID.randomUUID()))
                        .userName("student")
                        .password(passwordEncoder.encode("123456"))
                        .role(Role.USER)
                        .email("quanctse182750@fpt.edu.vn")
                        .createdAt(LocalDateTime.now())
                        .fullName("Toi la sinh vien")
                        .phone("0987654323")
                        .address("SE1701 - FPT University")
                        .image("")
                        .build();
                accountRepository.save(student);
            }
        };
    }
}
