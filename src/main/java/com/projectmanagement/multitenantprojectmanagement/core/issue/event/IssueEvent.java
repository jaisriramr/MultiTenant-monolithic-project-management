package com.projectmanagement.multitenantprojectmanagement.core.issue.event;

import java.util.UUID;

import org.springframework.context.ApplicationEvent;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class IssueEvent extends ApplicationEvent {
    private final UUID issueId;
    private final UUID reportId;

    public IssueEvent(Object source, UUID issueId, UUID reporterId) {
        super(source);
        this.issueId = issueId;
        this.reportId = reporterId;
    }

}
