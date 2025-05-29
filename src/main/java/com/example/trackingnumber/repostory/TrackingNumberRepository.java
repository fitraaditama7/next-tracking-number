package com.example.trackingnumber.repostory;

import com.example.trackingnumber.model.entity.TrackingNumber;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface TrackingNumberRepository extends JpaRepository<TrackingNumber, Long> {
    Optional<TrackingNumber> findByIdempotencyKey(String idempotencyKey);
}
