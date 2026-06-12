package com.skfkfkvlrm.stockgame_spring.domain;

public enum OrderStatus {
    PENDING,    //대기
    FILLED,     //체결
    CANCELED,   //취소
    SELL,       //매도
    BUY         //매수
}
