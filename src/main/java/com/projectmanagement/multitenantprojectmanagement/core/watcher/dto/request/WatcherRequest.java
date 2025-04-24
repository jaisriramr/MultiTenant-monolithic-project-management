package com.projectmanagement.multitenantprojectmanagement.core.watcher.dto.request;

import java.util.UUID;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class WatcherRequest {
    private UUID issueId;
    private UUID userId;
}
