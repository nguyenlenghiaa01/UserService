package com.example.UserService.Entity;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ForgotPasswordEvent {
    private String email;
    private String token;
}
