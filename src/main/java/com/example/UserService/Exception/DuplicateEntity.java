package com.example.UserService.Exception;

public class DuplicateEntity extends RuntimeException{
    public DuplicateEntity(String message){
        super(message);
    }
}
