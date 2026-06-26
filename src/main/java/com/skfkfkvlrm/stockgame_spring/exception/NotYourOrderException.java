package com.skfkfkvlrm.stockgame_spring.exception;

public class NotYourOrderException extends StockGameException {
    public NotYourOrderException() {
        super(ErrorCode.NOT_YOUR_ORDER);
    }
}