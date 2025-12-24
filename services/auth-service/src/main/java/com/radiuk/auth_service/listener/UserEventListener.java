package com.radiuk.auth_service.listener;

import com.radiuk.auth_service.event.UserCreatedEvent;
import com.radiuk.auth_service.event.UserAction;
import com.radiuk.auth_service.publisher.UserEventPublisher;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

@Component
@RequiredArgsConstructor
public class UserEventListener {

    private final UserEventPublisher userEventPublisher;

    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void handleUserCreated(UserCreatedEvent event) {
        userEventPublisher.publish(event, UserAction.CREATED);
    }

    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void handleUserUpdated(UserCreatedEvent event) {
        userEventPublisher.publish(event, UserAction.UPDATED);
    }

    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void handleUserDeleted(UserCreatedEvent event) {
        userEventPublisher.publish(event, UserAction.DELETED);
    }

}
