package com.velto.service;

import com.velto.model.Notification;

import java.util.List;

public interface NotificationService {

    List<Notification> getNotificationsByUserId(String userId);

    Notification markAsRead(String notificationId);
}
