package org.example.camunda.app.user;

import org.springframework.security.core.GrantedAuthority;

public enum Role implements GrantedAuthority {
    USER,
    DECLARER,
    EDITOR,
    MODERATOR,
    RESPONSIBLE,
    READER,
    ADMIN;

    @Override
    public String getAuthority() {
        return name();
    }

    public String toString() {
        return name();
    }
}
