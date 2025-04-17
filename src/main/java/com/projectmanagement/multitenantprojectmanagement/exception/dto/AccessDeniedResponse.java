package com.projectmanagement.multitenantprojectmanagement.exception.dto;

import java.time.LocalDateTime;
import java.util.List;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class AccessDeniedResponse {
    private LocalDateTime timestamp;
    private int status;
    private String error;
    private String message;
    private List<String> requiredScopes;
    private List<String> providedScopes;
    private String path;
}
