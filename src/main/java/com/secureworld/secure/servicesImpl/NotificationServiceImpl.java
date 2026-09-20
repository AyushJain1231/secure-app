package com.secureworld.secure.servicesImpl;

import com.secureworld.secure.Entity.AppUser;
import com.secureworld.secure.repository.AppUserRepository;
import com.secureworld.secure.services.NotificationService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

@Service
public class NotificationServiceImpl implements NotificationService {

    private static final Logger logger = LoggerFactory.getLogger(NotificationServiceImpl.class);

    private final AppUserRepository userRepository;

    public NotificationServiceImpl(AppUserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    @Async("notificationTaskExecutor")
    public void sendNotificationsAsync() {
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
