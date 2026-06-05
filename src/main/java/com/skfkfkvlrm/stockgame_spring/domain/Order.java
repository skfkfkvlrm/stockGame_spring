package com.skfkfkvlrm.stockgame_spring.domain;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
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
public class Order {
    @Id
    @GeneratedValue(strategy = jakarta.persistence.GenerationType.IDENTITY)
    private int orderId;
    private String content;
    private int price;
    private int amount;
    private OrderStatus state;
    @CreationTimestamp
    private LocalDateTime createdDate;
    @UpdateTimestamp
    private LocalDateTime updatedDate;
    private LocalDateTime deletedDate;

    @Override
    public String toString() {
        return "[id=" + orderId + ", content=" + content + ", price=" + price + ", amount=" + amount
                + ", state=" + state + ", createdDate=" + createdDate + "]";
    }
}
