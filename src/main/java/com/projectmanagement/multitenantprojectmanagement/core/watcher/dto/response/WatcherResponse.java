package com.projectmanagement.multitenantprojectmanagement.core.watcher.dto.response;

import java.util.UUID;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class WatcherResponse {
    private UUID id;
    private String userName;
    private String userProfilePic;
    private UUID userId;
}
