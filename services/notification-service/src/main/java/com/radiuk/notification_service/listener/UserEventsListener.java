package com.radiuk.notification_service.listener;

import com.radiuk.notification_service.config.RabbitConfig;
import com.radiuk.notification_service.event.UserNotificationMessage;
import com.radiuk.notification_service.service.EmailService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class UserEventsListener {

    private final EmailService emailService;

    @RabbitListener(queues = RabbitConfig.QUEUE_NAME)
    public void handleUserNotification(UserNotificationMessage message) {
        if (message == null || message.adminEmail() == null) {
            log.warn("Received invalid notification message: {}", message);
            return;
        }

        emailService.sendEmail(message.adminEmail(), message.subject(), message.text());
    }
}