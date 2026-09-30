package com.teammate.entity;

import jakarta.persistence.*;

import java.time.Instant;

// 项目实体，对应数据库里的 projects 表。
@Entity
@Table(name = "projects")
public class Project {

    // 项目的唯一 ID，由数据库自动生成。
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // 项目名称，例如 Project Alpha。
    @Column(nullable = false, length = 100)
    private String name;

    // 项目简介，可以为空。
    @Column(length = 500)
    private String description;

    // 项目创建时间。
    @Column(name = "created_at", nullable = false)
    private Instant createdAt;

    // JPA 使用的无参数构造方法。
    protected Project() {
    }

    // 创建项目时使用。
    public Project(String name, String description) {
        this.name = name;
        this.description = description;
        this.createdAt = Instant.now();
    }

    public Long getId() { return id; }
    public String getName() { return name; }
    public String getDescription() { return description; }
    public Instant getCreatedAt() { return createdAt; }
}
