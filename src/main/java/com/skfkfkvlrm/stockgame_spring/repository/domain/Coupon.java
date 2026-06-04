package com.skfkfkvlrm.stockgame_spring.repository.domain;

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
public class Coupon extends Student {
    @Id
    @GeneratedValue(generator = "coupon_id", strategy = GenerationType.IDENTITY)
    private Long couponId;
    private String name;
    private int price;
    @CreationTimestamp
    private LocalDateTime createdDate;
}
