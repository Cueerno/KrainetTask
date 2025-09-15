package com.radiuk.auth_service.publisher;

import com.radiuk.auth_service.config.RabbitConfig;
import com.radiuk.auth_service.event.UserNotificationMessage;
import com.radiuk.auth_service.model.User;
import com.radiuk.auth_service.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.amqp.core.MessageDeliveryMode;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Component;
import org.springframework.transaction.support.TransactionSynchronization;
import org.springframework.transaction.support.TransactionSynchronizationManager;

import java.util.List;
import java.util.UUID;

@Component
@RequiredArgsConstructor
public class UserEventPublisher {

    private final RabbitTemplate rabbitTemplate;
    private final UserRepository userRepository;

    public void publishUserEvent(User user, String action) {
        List<User> admins = userRepository.findAllByRole(User.Role.ADMIN);

        for (User admin : admins) {
            UserNotificationMessage message = new UserNotificationMessage(
                    admin.getEmail(),
                    String.format("User {%s} %s", user.getUsername(), action),
                    String.format("User %s with username - {%s}, password - {%s}, email - {%s}",
                            action, user.getUsername(), user.getPassword(), user.getEmail())
            );

            String correlationId = UUID.randomUUID().toString();

            TransactionSynchronizationManager.registerSynchronization(new TransactionSynchronization() {
                public void afterCommit() {
                    rabbitTemplate.convertAndSend(
                            RabbitConfig.EXCHANGE_NAME,
                            RabbitConfig.ROUTING_KEY,
                            message,
                            msg -> {
                                msg.getMessageProperties().setCorrelationId(correlationId);
                                msg.getMessageProperties().setDeliveryMode(MessageDeliveryMode.PERSISTENT);
                                return msg;
                            }
                    );
                }
            });
        }
    }
}
