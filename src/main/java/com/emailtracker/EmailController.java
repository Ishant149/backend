package com.emailtracker;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.view.RedirectView;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@RestController
@CrossOrigin(origins = "http://localhost:3000")
public class EmailController {
    
    @Autowired
    private EmailService emailService;
    
    @Autowired
    private EmailTrackRepository repository;
    
    @PostMapping("/api/send-email")
    public ResponseEntity<?> sendEmail(@RequestBody EmailRequest request) {
        try {
            String trackingId = emailService.sendTrackedEmail(request);
            return ResponseEntity.ok(Map.of(
                "success", true,
                "trackingId", trackingId,
                "message", "Email sent successfully"
            ));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of(
                "success", false,
                "error", "Failed to send email: " + e.getMessage()
            ));
        }
    }
    
    @GetMapping("/track/{trackingId}")
    public RedirectView trackClick(@PathVariable String trackingId) {
        System.out.println("Tracking click for ID: " + trackingId);
        
        Optional<EmailTrack> emailTrack = repository.findByTrackingId(trackingId);
        
        if (emailTrack.isPresent()) {
            EmailTrack track = emailTrack.get();
            System.out.println("Found email track: " + track.getEmail());
            track.setClicked(true);
            repository.save(track);
            System.out.println("Updated clicked status to true");
            
            return new RedirectView("http://localhost:3000?tracked=" + trackingId + "&status=success");
        }
        
        System.out.println("Tracking ID not found: " + trackingId);
        return new RedirectView("http://localhost:3000?error=invalid");
    }
    
    @GetMapping("/api/stats")
    public List<EmailTrack> getStats() {
        List<EmailTrack> stats = repository.findAll();
        System.out.println("Returning " + stats.size() + " email tracks");
        return stats;
    }
    
    @GetMapping("/api/track-info/{trackingId}")
    public ResponseEntity<EmailTrack> getTrackingInfo(@PathVariable String trackingId) {
        Optional<EmailTrack> emailTrack = repository.findByTrackingId(trackingId);
        
        if (emailTrack.isPresent()) {
            return ResponseEntity.ok(emailTrack.get());
        }
        
        return ResponseEntity.notFound().build();
    }
}