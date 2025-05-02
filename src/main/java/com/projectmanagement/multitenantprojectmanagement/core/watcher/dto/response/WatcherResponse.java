package com.projectmanagement.multitenantprojectmanagement.core.watcher.dto.response;

import java.util.UUID;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class WatcherResponse {
    private UUID id;
    private String userName;
    private String userProfilePic;
    private UUID userId;
}
