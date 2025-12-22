package com.radiuk.auth_service.event;

public record UserDeletedEvent(

        String username,
        String email

) implements UserEvent {
}
