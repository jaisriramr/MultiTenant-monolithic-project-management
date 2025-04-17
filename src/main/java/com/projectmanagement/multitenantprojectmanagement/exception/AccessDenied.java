package com.projectmanagement.multitenantprojectmanagement.exception;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

import lombok.Getter;

@ResponseStatus(HttpStatus.FORBIDDEN)
@Getter
public class AccessDenied extends RuntimeException {
    private final List<String> requiredScopes;
    private final List<String> providedScopes;

    public AccessDenied(String message, List<String> requiredScope, List<String> providedScopes) {
        super(message);
        this.requiredScopes = requiredScope;
        this.providedScopes = providedScopes;
    }

}
