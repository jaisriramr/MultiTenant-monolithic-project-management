package com.projectmanagement.multitenantprojectmanagement.core.watcher.dto.request;

import java.util.UUID;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class WatcherRequest {
    private UUID issueId;
    private UUID userId;
}
