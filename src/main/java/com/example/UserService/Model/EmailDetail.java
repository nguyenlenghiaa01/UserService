package com.example.UserService.Model;

import com.example.UserService.Enity.Account;
import lombok.Data;

@Data
public class EmailDetail {
    Account receiver;
    String subject;
    String link;
}
