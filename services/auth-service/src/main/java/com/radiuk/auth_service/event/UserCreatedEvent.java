package com.radiuk.auth_service.event;

public record UserCreatedEvent(

        String username,
        String email

) implements UserEvent {
}
