package com.radiuk.auth_service.event;

public sealed interface UserEvent permits UserCreatedEvent, UserUpdatedEvent, UserDeletedEvent {

    String username();

    String email();
}
