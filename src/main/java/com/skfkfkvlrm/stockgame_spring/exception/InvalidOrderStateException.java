package com.skfkfkvlrm.stockgame_spring.exception;

public class InvalidOrderStateException extends StockGameException {
    public InvalidOrderStateException() {
        super(ErrorCode.INVALID_ORDER_STATE);
    }
}