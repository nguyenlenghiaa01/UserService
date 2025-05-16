package com.example.UserService.Model.Response;

import com.example.UserService.Enum.Role;
import lombok.Data;

@Data
public class AccountResponse {
    String uuid;
    String userName;
    String fullName;
    String phone;
    String email;
    String address;
    Role role;
    String token;
    String image;
}
