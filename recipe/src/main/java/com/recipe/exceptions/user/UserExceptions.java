package com.recipe.exceptions.user;

import lombok.Getter;

@Getter
public enum UserExceptions {
    NOT_FOUND("NOT_FOUND", 404);

    private UserException userException;

    UserExceptions(String message, int code) {
        userException = new UserException(message, code);
    }

    public UserException getUserException() {
        return userException;
    }
}
