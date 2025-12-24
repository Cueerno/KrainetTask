package com.radiuk.auth_service.publisher;

import com.radiuk.auth_service.config.RabbitConfig;
import com.radiuk.auth_service.event.UserEvent;
import com.radiuk.auth_service.event.UserAction;
import com.radiuk.auth_service.model.UserNotificationMessage;
import com.radiuk.auth_service.model.User;
import com.radiuk.auth_service.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Component;

import java.util.List;

@Slf4j
@Component
@RequiredArgsConstructor
public class UserEventPublisher {

    private final RabbitTemplate rabbitTemplate;
    private final UserRepository userRepository;

    public void publish(UserEvent userEvent, UserAction userAction) {
        List<User> admins = userRepository.findAllByRole(User.Role.ADMIN);

        for (User admin : admins) {
            UserNotificationMessage message = new UserNotificationMessage(
                    admin.getEmail(),
                    String.format("User {%s} %s", userEvent.username(), userAction.name()),
                    String.format("User %s with username - {%s}, email - {%s}",
                            userAction.name(), userEvent.username(),userEvent.email())
            );

            rabbitTemplate.convertAndSend(
                    RabbitConfig.EXCHANGE_NAME,
                    RabbitConfig.ROUTING_KEY,
                    message
            );
        }
    }
}
