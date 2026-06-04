package com.skfkfkvlrm.stockgame_spring.repository.domain;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;

@Setter
@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Entity
public class CouponPurchase extends Coupon {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long couponPurchaseId;
    private int price;
    private String name;
    private CouponPurchaseStatus state;
    @CreationTimestamp
    private LocalDateTime createdDate;
    @UpdateTimestamp
    private LocalDateTime updatedDate;
}
