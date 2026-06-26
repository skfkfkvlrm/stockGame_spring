package com.skfkfkvlrm.stockgame_spring.exception;

public class InsufficientStockException extends StockGameException {
    public InsufficientStockException() {
        super(ErrorCode.INSUFFICIENT_STOCK);
    }
}