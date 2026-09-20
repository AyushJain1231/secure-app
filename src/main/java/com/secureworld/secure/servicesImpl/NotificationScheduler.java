package com.secureworld.secure.servicesImpl;

import com.secureworld.secure.Entity.AppUser;
import com.secureworld.secure.repository.AppUserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

@Service
public class NotificationScheduler {

    private static final Logger logger = LoggerFactory.getLogger(NotificationScheduler.class);
    private static final long NOTIFICATION_INTERVAL_MILLIS = 5 * 60 * 1000L;

    private final AppUserRepository userRepository;

    public NotificationScheduler(AppUserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Scheduled(fixedRate = NOTIFICATION_INTERVAL_MILLIS)
    public void sendScheduledNotifications() {
        for (AppUser user : userRepository.findAll()) {
            try {
                logger.info("Notification for user ID {} at email {}",
                        user.getUserId(), user.getEmail());
            } catch (RuntimeException exception) {
                logger.error("Failed to create notification for user ID {} at email {}",
                        user.getUserId(), user.getEmail(), exception);
            }
        }
    }
}
