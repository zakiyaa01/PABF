package com.unand.asrama.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Service
public class NotificationService {

    private static final Logger log = LoggerFactory.getLogger(NotificationService.class);

    public void sendPushNotification(String userIdentifier, String title, String message) {
        // FCM Push Notification Dispatcher simulation / integration
        log.info("📲 [FCM PUSH NOTIFICATION] To: {} | Title: '{}' | Body: '{}'", userIdentifier, title, message);
    }
}
