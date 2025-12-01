package com.emailtracker;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "email_tracks")
public class EmailTrack {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(unique = true, nullable = false)
    private String trackingId;
    
    @Column(nullable = false)
    private String email;
    
    @Column(nullable = false)
    private String subject;
    
    private String message;
    
    @Column(nullable = false)
    private boolean clicked = false;
    
    @Column(nullable = false)
    private LocalDateTime sentAt = LocalDateTime.now();
    
    private LocalDateTime clickedAt;
    
    // Constructors
    public EmailTrack() {}
    
    public EmailTrack(String trackingId, String email, String subject, String message) {
        this.trackingId = trackingId;
        this.email = email;
        this.subject = subject;
        this.message = message;
    }
    
    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    
    public String getTrackingId() { return trackingId; }
    public void setTrackingId(String trackingId) { this.trackingId = trackingId; }
    
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
    
    public String getSubject() { return subject; }
    public void setSubject(String subject) { this.subject = subject; }
    
    public String getMessage() { return message; }
    public void setMessage(String message) { this.message = message; }
    
    public boolean isClicked() { return clicked; }
    public void setClicked(boolean clicked) { 
        this.clicked = clicked;
        if (clicked && clickedAt == null) {
            this.clickedAt = LocalDateTime.now();
        }
    }
    
    public LocalDateTime getSentAt() { return sentAt; }
    public void setSentAt(LocalDateTime sentAt) { this.sentAt = sentAt; }
    
    public LocalDateTime getClickedAt() { return clickedAt; }
    public void setClickedAt(LocalDateTime clickedAt) { this.clickedAt = clickedAt; }
}