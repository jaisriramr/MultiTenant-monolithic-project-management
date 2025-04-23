package com.projectmanagement.multitenantprojectmanagement.core.issue;

import java.time.Instant;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;

import org.hibernate.annotations.UuidGenerator;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import com.projectmanagement.multitenantprojectmanagement.core.comment.Comment;
import com.projectmanagement.multitenantprojectmanagement.core.epic.Epic;
import com.projectmanagement.multitenantprojectmanagement.core.issue.enums.IssuePriority;
import com.projectmanagement.multitenantprojectmanagement.core.issue.enums.IssueStatus;
import com.projectmanagement.multitenantprojectmanagement.core.issue.enums.IssueType;
import com.projectmanagement.multitenantprojectmanagement.core.label.Label;
import com.projectmanagement.multitenantprojectmanagement.core.project.Projects;
import com.projectmanagement.multitenantprojectmanagement.core.sprint.Sprint;
import com.projectmanagement.multitenantprojectmanagement.core.subtask.SubTask;
import com.projectmanagement.multitenantprojectmanagement.core.watcher.Watcher;
import com.projectmanagement.multitenantprojectmanagement.core.worklog.WorkLog;
import com.projectmanagement.multitenantprojectmanagement.users.Users;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import lombok.ToString;

@Entity
@Data
@RequiredArgsConstructor
@EntityListeners(AuditingEntityListener.class)
@Table(name = "issues")
public class Issue {
    @Id
    @UuidGenerator
    private UUID id;

    private String key;

    private String title;

    @Column(columnDefinition = "TEXT")
    private String description;

    @Enumerated(EnumType.STRING)
    private IssueStatus status;

    @Enumerated(EnumType.STRING)
    private IssueType type;

    @Enumerated(EnumType.STRING)
    private IssuePriority priority;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "project_id")
    @JsonBackReference
    @ToString.Exclude
    private Projects project;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "sprint_id")
    @ToString.Exclude
    private Sprint sprint;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "assignee_id")
    @ToString.Exclude
    private Users assignee;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "reporter_id")
    @ToString.Exclude
    private Users reporter;

    @ManyToMany
    @JoinTable(name = "issue_labels", 
                joinColumns = @JoinColumn(name ="issue_id"), 
                inverseJoinColumns = @JoinColumn(name="label_id"))
    @JsonManagedReference
    @ToString.Exclude
    private Set<Label> labels = new HashSet<>();

    private Integer storyPoints;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "epic_id")
    @ToString.Exclude
    private Epic epic;

    @OneToMany(mappedBy = "parentIssue", cascade = CascadeType.ALL)
    @ToString.Exclude
    private List<SubTask> subTasks = new ArrayList<>();

    @OneToMany(mappedBy = "issue", cascade = CascadeType.ALL)
    @ToString.Exclude
    private List<WorkLog> workLogs = new ArrayList<>();

    @OneToMany(mappedBy = "issue", cascade = CascadeType.ALL)
    @ToString.Exclude
    private List<Watcher> watchers = new ArrayList<>();

    @OneToMany(mappedBy = "issue", cascade = CascadeType.ALL)
    @ToString.Exclude
    private List<Comment> comments = new ArrayList<>();

    @CreatedDate
    @Column(name = "created_at", updatable = false, nullable = false)
    private Instant createdAt;

    @LastModifiedDate
    @Column(name = "updated_at")
    private Instant updatedAt;
}
