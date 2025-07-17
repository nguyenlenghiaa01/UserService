package com.example.UserService.Config;

import com.example.UserService.Enity.Account;
import com.example.UserService.Enum.Role;
import com.example.UserService.Repository.AccountRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class InitialData {
    @Bean
    public CommandLineRunner commandLineRunner(AccountRepository accountRepository) {
        return args -> {
            // Initialize the application repository with some data
            if(accountRepository.findAll().isEmpty()) {
                Account admin = Account.builder()
                        .userName("admin")
                        .password("123456")
                        .role(Role.ADMIN)
                        .email("nghialncse170125@fpt.edu.vn")
                        .fullName("Admin user")
                        .phone("0987654321")
                        .address("FPT Admission System")
                        .image("")
                        .build();
                accountRepository.save(admin);

                Account consultant = Account.builder()
                        .userName("consultant")
                        .password("123456")
                        .role(Role.CONSULTANT)
                        .email("sonnqse182727@fpt.edu.vn")
                        .fullName("Consultant user")
                        .phone("0987654322")
                        .address("FPT Admission System")
                        .image("")
                        .build();
                accountRepository.save(consultant);

                Account student = Account.builder()
                        .userName("student")
                        .password("123456")
                        .role(Role.USER)
                        .email("quanctse182750@fpt.edu.vn")
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
