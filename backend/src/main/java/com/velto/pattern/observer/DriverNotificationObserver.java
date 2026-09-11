package com.velto.pattern.observer;

import com.velto.model.Notification;
import com.velto.repository.NotificationRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

/**
 * Concrete Observer notifying the driver when their ride status changes.
 */
@Component
public class DriverNotificationObserver implements RideObserver {

    private static final Logger log = LoggerFactory.getLogger(DriverNotificationObserver.class);
    private final NotificationRepository notificationRepository;

    public DriverNotificationObserver(NotificationRepository notificationRepository) {
        this.notificationRepository = notificationRepository;
    }

    @Override
    public void onRideEvent(RideEvent event) {
        if (event.getRide() == null || event.getRide().getDriverId() == null) {
            return;
        }

        String message = String.format("Driver Alert: Your ride (%s -> %s) status updated to %s.",
                event.getRide().getPickup(), event.getRide().getDestination(), event.getNewStatus());

        Notification notification = new Notification(
                event.getRide().getDriverId(),
                message,
                "DRIVER_RIDE_UPDATE"
        );
        notificationRepository.save(notification);
        log.info("Driver notified [Driver: {}, Status: {}]", event.getRide().getDriverId(), event.getNewStatus());
    }
}
