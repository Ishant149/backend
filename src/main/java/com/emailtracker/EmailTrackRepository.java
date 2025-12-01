package com.emailtracker;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.Optional;

@Repository
public interface EmailTrackRepository extends JpaRepository<EmailTrack, Long> {
    Optional<EmailTrack> findByTrackingId(String trackingId);
}