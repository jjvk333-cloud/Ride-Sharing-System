package com.velto.pattern.observer;

import com.velto.model.Notification;
import com.velto.model.Role;
import com.velto.model.User;
import com.velto.repository.NotificationRepository;
import com.velto.repository.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * Concrete Observer recording system audit alerts for admins upon critical events (e.g. Cancellations, Completions).
 */
@Component
public class AdminNotificationObserver implements RideObserver {

    private static final Logger log = LoggerFactory.getLogger(AdminNotificationObserver.class);
    private final UserRepository userRepository;
    private final NotificationRepository notificationRepository;

    public AdminNotificationObserver(UserRepository userRepository, NotificationRepository notificationRepository) {
        this.userRepository = userRepository;
        this.notificationRepository = notificationRepository;
    }

    @Override
    public void onRideEvent(RideEvent event) {
        List<User> admins = userRepository.findByRole(Role.ADMIN);
        if (admins.isEmpty()) {
            return;
        }

        String message = String.format("System Audit: Ride '%s' transitioned to %s.",
                event.getRide().getId(), event.getNewStatus());

        for (User admin : admins) {
            Notification notification = new Notification(
                    admin.getId(),
                    message,
                    "SYSTEM_AUDIT"
            );
            notificationRepository.save(notification);
            log.info("Admin notified [Admin: {}, Event: {}]", admin.getId(), event.getNewStatus());
        }
    }
}
