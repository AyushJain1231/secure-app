package com.secureworld.secure.controller;

import com.secureworld.secure.services.NotificationService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.concurrent.CompletableFuture;

@RestController
@RequestMapping("/api/notifications")
public class NotificationController {

    private static final Logger logger = LoggerFactory.getLogger(NotificationController.class);

    private final NotificationService notificationService;

    public NotificationController(
            @Qualifier("notificationServiceV2Impl") NotificationService notificationService) {
        this.notificationService = notificationService;
    }

    @PostMapping("/send")
    public ResponseEntity<Void> sendNotifications() {
        CompletableFuture<Void> notificationResult = notificationService.sendNotificationsAsync();
        notificationResult.whenComplete((result, exception) -> {
            if (exception != null) {
                logger.error("Notification request failed", exception);
            } else {
                logger.info("Notification request completed successfully");
            }
        });
        return ResponseEntity.accepted().build();
    }
}
