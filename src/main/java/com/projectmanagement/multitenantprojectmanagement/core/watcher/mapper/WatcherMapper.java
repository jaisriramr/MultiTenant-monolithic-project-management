package com.projectmanagement.multitenantprojectmanagement.core.watcher.mapper;

import java.util.ArrayList;
import java.util.List;

import com.projectmanagement.multitenantprojectmanagement.core.watcher.Watcher;
import com.projectmanagement.multitenantprojectmanagement.core.watcher.dto.response.WatcherResponse;

public class WatcherMapper {

    public static WatcherResponse toWatcherResponse(Watcher watcher) {
        return WatcherResponse.builder()
                .id(watcher.getId())
                .userName(watcher.getUser().getName())
                .userProfilePic(watcher.getUser().getProfilePic())
                .userId(watcher.getUser().getId())
                .build();
    }

    public static List<WatcherResponse> toListWatcherResponses(List<Watcher> watchers) {

        List<WatcherResponse> response = new ArrayList<>();

        for(Watcher watcher: watchers) {
            response.add(toWatcherResponse(watcher));
        }

        return response;

    }

}
