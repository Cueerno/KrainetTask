package com.radiuk.auth_service.model;

public record UserNotificationMessage(
        String adminEmail,
        String subject,
        String text
) {}