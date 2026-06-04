package com.skfkfkvlrm.stockgame_spring.repository.domain;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

@Setter
@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Entity
public class News {
    @Id
    @GeneratedValue(generator = "news_id", strategy = jakarta.persistence.GenerationType.IDENTITY)
    private Long newsId;
    private String content;
    @CreationTimestamp
    private LocalDateTime createdDate;

    @Override
    public String toString() {
        return "[newsNO=" + newsId + ", content=" + content + "]";
    }
}
