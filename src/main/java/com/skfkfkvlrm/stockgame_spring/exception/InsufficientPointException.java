package com.skfkfkvlrm.stockgame_spring.exception;

public class InsufficientPointException extends StockGameException {
    public InsufficientPointException() {
        super(ErrorCode.INSUFFICIENT_POINT);
    }
}