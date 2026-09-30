package com.teammate.entity;

import jakarta.persistence.*;

import java.time.Instant;

@Entity
@Table(name = "project_members", uniqueConstraints = {
        @UniqueConstraint(columnNames = {"project_id", "user_id"})
})
public class ProjectMember {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "project_id", nullable = false)
    private Project project;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(nullable = false, length = 30)
    private String role;

    @Column(name = "joined_at", nullable = false)
    private Instant joinedAt;

    protected ProjectMember() {
    }

    public ProjectMember(Project project, User user, String role) {
        this.project = project;
        this.user = user;
        this.role = role;
        this.joinedAt = Instant.now();
    }

    public Long getId() { return id; }
    public Project getProject() { return project; }
    public User getUser() { return user; }
    public String getRole() { return role; }
    public Instant getJoinedAt() { return joinedAt; }
}
