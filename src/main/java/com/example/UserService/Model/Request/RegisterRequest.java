package com.example.UserService.Model.Request;

import com.example.UserService.Enum.Role;
import jakarta.persistence.Column;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class RegisterRequest {

    @NotBlank(message = "UserName cannot be blank")
    @Pattern(regexp = "^\\S+$", message = "username cannot have space!")
    @Column(name = "user_name", unique = true)
    private String userName;

    @Email(message = "Invalid Email!")
    @Column(unique = true)
    String email;

    @Pattern(regexp = "(84|0[3|5|7|8|9])+(\\d{8})", message = "Invalid phone!")
    @Column(unique = true)
    String phone;

    @Size(min = 6, message = "Password must be at least 6 character!")
    String password;

    @NotBlank(message = "Name cannot be blank")
    @Pattern(regexp = "^[^\\d]*$", message = "Name cannot contain numbers!")
    private String fullName;

    @NotBlank(message = "Address cannot be blank")
    private String address;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    Role role;
}