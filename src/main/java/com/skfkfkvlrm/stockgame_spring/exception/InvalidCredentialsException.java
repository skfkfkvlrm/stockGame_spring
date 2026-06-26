package com.skfkfkvlrm.stockgame_spring.exception;

public class InvalidCredentialsException extends StockGameException {
    public InvalidCredentialsException() {
        super(ErrorCode.INVALID_CREDENTIALS);
    }
}