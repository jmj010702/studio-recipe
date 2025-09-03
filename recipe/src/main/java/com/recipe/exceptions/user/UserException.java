package com.recipe.exceptions.user;

public class UserException extends RuntimeException {
    String message;
    int code;

    public UserException(String message, int code) {
        super(message);
        this.message = message;
        this.code = code;
    }
}
