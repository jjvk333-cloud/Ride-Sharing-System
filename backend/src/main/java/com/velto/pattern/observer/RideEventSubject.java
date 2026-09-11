package com.velto.pattern.observer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

/**
 * Concrete Subject in the GoF Observer Pattern.
 * Manages registered observers and broadcasts ride lifecycle events.
 */
@Component
public class RideEventSubject {

    private static final Logger log = LoggerFactory.getLogger(RideEventSubject.class);
    private final List<RideObserver> observers = new ArrayList<>();

    public RideEventSubject(List<RideObserver> initialObservers) {
        if (initialObservers != null) {
            observers.addAll(initialObservers);
        }
    }

    public synchronized void attach(RideObserver observer) {
        if (observer != null && !observers.contains(observer)) {
            observers.add(observer);
            log.info("Observer attached: {}", observer.getClass().getSimpleName());
        }
    }

    public synchronized void detach(RideObserver observer) {
        observers.remove(observer);
        log.info("Observer detached: {}", observer.getClass().getSimpleName());
    }

    public void notifyObservers(RideEvent event) {
        log.info("Broadcasting ride event [{} -> {}] to {} observers...",
                event.getPreviousStatus(), event.getNewStatus(), observers.size());

        for (RideObserver observer : observers) {
            try {
                observer.onRideEvent(event);
            } catch (Exception e) {
                log.error("Error executing observer {}: {}", observer.getClass().getSimpleName(), e.getMessage(), e);
            }
        }
    }
}
