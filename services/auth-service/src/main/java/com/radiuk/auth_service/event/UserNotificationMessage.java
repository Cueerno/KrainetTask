package com.radiuk.auth_service.event;

public record UserNotificationMessage(
        String adminEmail,
        String subject,
        String text
) {}