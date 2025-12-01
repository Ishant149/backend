const express = require('express');
const cors = require('cors');
const sqlite3 = require('sqlite3').verbose();
const nodemailer = require('nodemailer');
const { v4: uuidv4 } = require('uuid');
const path = require('path');

const app = express();
const PORT = 5000;

// Middleware
app.use(cors());
app.use(express.json());

// Database setup
const db = new sqlite3.Database('./email_tracker.db');

// Create table
db.serialize(() => {
    db.run(`CREATE TABLE IF NOT EXISTS email_tracks (
        id INTEGER PRIMARY KEY AUTOINCREMENT,
        tracking_id TEXT UNIQUE,
        email TEXT,
        subject TEXT,
        clicked BOOLEAN DEFAULT 0,
        sent_at DATETIME DEFAULT CURRENT_TIMESTAMP,
        clicked_at DATETIME
    )`);
});

// Email transporter (configure with your email)
const transporter = nodemailer.createTransporter({
    service: 'gmail',
    auth: {
        user: 'your-email@gmail.com', // Replace with your email
        pass: 'your-app-password'     // Replace with your app password
    }
});

// Routes

// Send tracked email
app.post('/api/send-email', (req, res) => {
    const { to, subject, message } = req.body;
    const trackingId = uuidv4();
    
    // Save to database
    db.run(
        'INSERT INTO email_tracks (tracking_id, email, subject) VALUES (?, ?, ?)',
        [trackingId, to, subject],
        function(err) {
            if (err) {
                return res.status(500).json({ error: 'Database error' });
            }
            
            // Create tracking link
            const trackingLink = `http://localhost:5000/track/${trackingId}`;
            
            // Email content with tracking link
            const emailContent = `
                <div style="font-family: Arial, sans-serif; max-width: 600px; margin: 0 auto;">
                    <h2>${subject}</h2>
                    <p>${message}</p>
                    <br>
                    <p><a href="${trackingLink}" style="background: #007bff; color: white; padding: 10px 20px; text-decoration: none; border-radius: 5px;">Click Here to View Full Message</a></p>
                    <br>
                    <p style="font-size: 12px; color: #666;">This email contains a tracking pixel for delivery confirmation.</p>
                </div>
            `;
            
            // Send email
            const mailOptions = {
                from: 'your-email@gmail.com',
                to: to,
                subject: subject,
                html: emailContent
            };
            
            transporter.sendMail(mailOptions, (error, info) => {
                if (error) {
                    return res.status(500).json({ error: 'Failed to send email' });
                }
                
                res.json({ 
                    success: true, 
                    trackingId: trackingId,
                    message: 'Email sent successfully'
                });
            });
        }
    );
});

// Track email click
app.get('/track/:trackingId', (req, res) => {
    const { trackingId } = req.params;
    
    // Update database
    db.run(
        'UPDATE email_tracks SET clicked = 1, clicked_at = CURRENT_TIMESTAMP WHERE tracking_id = ?',
        [trackingId],
        function(err) {
            if (err) {
                console.error('Database error:', err);
            }
            
            // Redirect to frontend with tracking info
            res.redirect(`http://localhost:3000?tracked=${trackingId}&status=success`);
        }
    );
});

// Get all email statistics
app.get('/api/stats', (req, res) => {
    db.all('SELECT * FROM email_tracks ORDER BY sent_at DESC', (err, rows) => {
        if (err) {
            return res.status(500).json({ error: 'Database error' });
        }
        res.json(rows);
    });
});

// Get specific tracking info
app.get('/api/track-info/:trackingId', (req, res) => {
    const { trackingId } = req.params;
    
    db.get('SELECT * FROM email_tracks WHERE tracking_id = ?', [trackingId], (err, row) => {
        if (err) {
            return res.status(500).json({ error: 'Database error' });
        }
        if (!row) {
            return res.status(404).json({ error: 'Tracking ID not found' });
        }
        res.json(row);
    });
});

app.listen(PORT, () => {
    console.log(`🚀 Backend server running on http://localhost:${PORT}`);
    console.log('📧 Email tracking system ready!');
});