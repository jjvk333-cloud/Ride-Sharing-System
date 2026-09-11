package com.velto.pattern.observer;

import com.velto.model.Booking;
import com.velto.model.Notification;
import com.velto.repository.BookingRepository;
import com.velto.repository.NotificationRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * Concrete Observer notifying booked passengers when ride status updates.
 */
@Component
public class PassengerNotificationObserver implements RideObserver {

    private static final Logger log = LoggerFactory.getLogger(PassengerNotificationObserver.class);
    private final BookingRepository bookingRepository;
    private final NotificationRepository notificationRepository;

    public PassengerNotificationObserver(BookingRepository bookingRepository, NotificationRepository notificationRepository) {
        this.bookingRepository = bookingRepository;
        this.notificationRepository = notificationRepository;
    }

    @Override
    public void onRideEvent(RideEvent event) {
        if (event.getRide() == null || event.getRide().getId() == null) {
            return;
        }

        List<Booking> bookings = bookingRepository.findByRideId(event.getRide().getId());
        for (Booking booking : bookings) {
            String message = String.format("Ride update: Your ride from '%s' to '%s' is now %s.",
                    event.getRide().getPickup(), event.getRide().getDestination(), event.getNewStatus());

            Notification notification = new Notification(
                    booking.getPassengerId(),
                    message,
                    "RIDE_STATUS_CHANGE"
            );
            notificationRepository.save(notification);
            log.info("Passenger notified [Passenger: {}, Status: {}]", booking.getPassengerId(), event.getNewStatus());
        }
    }
}
