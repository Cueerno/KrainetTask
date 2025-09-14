package com.radiuk.notification_service.event;

public record UserNotificationMessage(
        String adminEmail,
        String subject,
        String text
) {}