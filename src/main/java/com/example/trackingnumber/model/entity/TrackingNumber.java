package com.example.trackingnumber.model.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.OffsetDateTime;

@Entity
@Table(name = "tracking_number")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TrackingNumber {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "idempotency_key", nullable = false, unique = true, length = 16)
    private String idempotencyKey;

    @Column(name = "tracking_code", nullable = false, unique = true, length = 16)
    private String trackingCode;

    @Column(name = "created_at", nullable = false)
    private OffsetDateTime createdAt;

    @PrePersist
    public void prePersist() {
        if (createdAt == null) {
            createdAt = OffsetDateTime.now();
        }
    }
}
