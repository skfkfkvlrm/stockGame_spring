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
public class GetPoint {
    @Id
    @GeneratedValue(strategy = jakarta.persistence.GenerationType.IDENTITY)
    private int getPointId;
    private String content;
    private int point;
    @CreationTimestamp
    private LocalDateTime createdDate;
}
