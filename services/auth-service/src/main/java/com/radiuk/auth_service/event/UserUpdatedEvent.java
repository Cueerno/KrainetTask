package com.radiuk.auth_service.event;

public record UserUpdatedEvent(

        String username,
        String email

) implements UserEvent {
}
