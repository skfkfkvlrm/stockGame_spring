package com.skfkfkvlrm.stockgame_spring.domain;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

@Setter
@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Entity
public class Coupon {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int couponId;
    private String name;
    private int price;
    @CreationTimestamp
    private LocalDateTime createdDate;
}
