package com.skfkfkvlrm.stockgame_spring.exception;

public class UnauthorizedAccessException extends StockGameException {
    public UnauthorizedAccessException() {
        super(ErrorCode.UNAUTHORIZED);
    }
}