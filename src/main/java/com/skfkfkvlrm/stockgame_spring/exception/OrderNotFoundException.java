package com.skfkfkvlrm.stockgame_spring.exception;

public class OrderNotFoundException extends StockGameException {
    public OrderNotFoundException() {
        super(ErrorCode.ORDER_NOT_FOUND);
    }
}