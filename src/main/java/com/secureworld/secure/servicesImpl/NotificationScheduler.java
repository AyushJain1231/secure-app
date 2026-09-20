package com.secureworld.secure.servicesImpl;

import com.secureworld.secure.services.NotificationService;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

@Service
public class NotificationScheduler {

    private static final long NOTIFICATION_INTERVAL_MILLIS = 5 * 60 * 1000L;

    private final NotificationService notificationService;

    public NotificationScheduler(
            @Qualifier("notificationServiceImpl") NotificationService notificationService) {
        this.notificationService = notificationService;
    }

    @Scheduled(fixedRate = NOTIFICATION_INTERVAL_MILLIS)
    public void sendScheduledNotifications() {
        notificationService.sendNotificationsAsync();
    }
}
