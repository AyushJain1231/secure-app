package com.secureworld.secure.servicesImpl;

import com.secureworld.secure.services.NotificationService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

@Service
public class NotificationScheduler {

    private static final Logger logger = LoggerFactory.getLogger(NotificationScheduler.class);
    private static final long NOTIFICATION_INTERVAL_MILLIS = 5 * 60 * 1000L;

    private final NotificationService notificationService;

    public NotificationScheduler(
            @Qualifier("notificationServiceImpl") NotificationService notificationService) {
        this.notificationService = notificationService;
    }

    @Scheduled(fixedRate = NOTIFICATION_INTERVAL_MILLIS)
    public void sendScheduledNotifications() {
        notificationService.sendNotificationsAsync()
                .whenComplete((result, exception) -> {
                    if (exception != null) {
                        logger.error("Scheduled notification processing failed", exception);
                    } else {
                        logger.info("Scheduled notification processing completed");
                    }
                });
    }
}
