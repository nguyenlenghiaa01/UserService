package com.example.UserService.Model.Request;

import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class ResetPasswordRequest {
    @Size(min = 6, message = "Password must be at least 6 character!")
    String password;

}
