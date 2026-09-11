package com.velto.pattern.observer;

/**
 * =========================================================================
 * DESIGN PATTERN 6 — OBSERVER PATTERN
 * =========================================================================
 * Observer interface for reacting to ride status lifecycle events.
 */
public interface RideObserver {

    void onRideEvent(RideEvent event);
}
