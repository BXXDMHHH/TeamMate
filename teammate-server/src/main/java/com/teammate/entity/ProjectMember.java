package com.teammate.entity;

import jakarta.persistence.*;

import java.time.Instant;

// ProjectMember 表示“某个用户加入了某个项目”。
// 它解决的是 Project 和 User 之间的多对多关系。
@Entity
@Table(name = "project_members", uniqueConstraints = {
        // 同一个用户不能重复加入同一个项目。
        @UniqueConstraint(columnNames = {"project_id", "user_id"})
})
public class ProjectMember {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // 多个 ProjectMember 可以属于同一个 Project。
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "project_id", nullable = false)
    private Project project;

    // 多个 ProjectMember 也可以属于同一个 User。
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    // 用户在项目中的角色，例如 OWNER、MEMBER。
    @Column(nullable = false, length = 30)
    private String role;

    // 加入项目的时间。
    @Column(name = "joined_at", nullable = false)
    private Instant joinedAt;

    // JPA 使用的无参数构造方法。
    protected ProjectMember() {
    }

    // 创建项目成员关系时使用。
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
