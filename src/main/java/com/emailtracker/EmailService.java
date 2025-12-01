package com.emailtracker;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import java.util.UUID;

@Service
public class EmailService {
    
    @Autowired
    private JavaMailSender mailSender;
    
    @Autowired
    private EmailTrackRepository repository;
    
    public String sendTrackedEmail(EmailRequest request) throws MessagingException {
        String trackingId = UUID.randomUUID().toString();
        
        // Save to database
        EmailTrack emailTrack = new EmailTrack(trackingId, request.getTo(), request.getSubject(), request.getMessage());
        repository.save(emailTrack);
        
        // Create tracking link
        String trackingLink = "http://localhost:5000/track/" + trackingId;
        
        // Create email content
        String emailContent = String.format("""
            <div style="font-family: Arial, sans-serif; max-width: 600px; margin: 0 auto;">
                <h2>%s</h2>
                <p>%s</p>
                <br>
                <p><a href="%s" style="background: #007bff; color: white; padding: 10px 20px; text-decoration: none; border-radius: 5px;">Click Here to View Full Message</a></p>
                <br>
                <p style="font-size: 12px; color: #666;">This email contains tracking for delivery confirmation.</p>
            </div>
            """, request.getSubject(), request.getMessage(), trackingLink);
        
        // Send email
        MimeMessage message = mailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(message, true);
        
        helper.setTo(request.getTo());
        helper.setSubject(request.getSubject());
        helper.setText(emailContent, true);
        helper.setFrom("noreply@emailtracker.com");
        
        mailSender.send(message);
        
        return trackingId;
    }
}