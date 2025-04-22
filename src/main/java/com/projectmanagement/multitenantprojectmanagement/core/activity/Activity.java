package com.projectmanagement.multitenantprojectmanagement.core.activity;

import java.time.Instant;
import java.util.UUID;

import org.hibernate.annotations.UuidGenerator;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import com.projectmanagement.multitenantprojectmanagement.core.activity.enums.EntityType;
import com.projectmanagement.multitenantprojectmanagement.core.issue.Issue;
import com.projectmanagement.multitenantprojectmanagement.users.Users;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.Data;
import lombok.RequiredArgsConstructor;

@Entity
@Data
@RequiredArgsConstructor
@EntityListeners(AuditingEntityListener.class)
@Table(name = "Activities")
public class Activity {

    @Id
    @UuidGenerator
    private UUID id;

    private String action;// e.g., STATUS_UPDATED, COMMENT_ADDED

    private String description; //"Sriram changed status from TODO to IN_PROGRESS"

    private String fieldChanged; // optional: "status", "assignee", etc.

    private String oldValue;

    private String newValue;

    @Enumerated(EnumType.STRING)
    private EntityType entityType; // "Issue", "Project", "Sprint", "Comment"

    private UUID entityId;     // the ID of the entity

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "performed_by")
    private Users performedBy;

    @CreatedDate
    @Column(name = "created_at", updatable = false, nullable = false)
    private Instant createdAt;

    @LastModifiedDate
    @Column(name = "updated_at")
    private Instant updatedAt;
}
