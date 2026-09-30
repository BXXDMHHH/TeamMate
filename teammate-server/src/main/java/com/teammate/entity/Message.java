package com.teammate.entity;

import jakarta.persistence.*;

import java.time.Instant;

// 聊天消息实体，对应数据库里的 messages 表。
@Entity
@Table(name = "messages")
public class Message {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // 这条消息属于哪个项目。
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "project_id", nullable = false)
    private Project project;

    // 消息是谁发送的。
    // 这里允许为空，因为以后 AI 消息没有普通 User。
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;

    // 消息正文，例如“大家好，我开始测试 TeamMate 了”。
    @Column(nullable = false, length = 4000)
    private String content;

    // 消息类型：USER 或 AI。
    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 10)
    private MessageType type;

    // 消息创建时间。
    @Column(name = "created_at", nullable = false)
    private Instant createdAt;

    // JPA 使用的无参数构造方法。
    protected Message() {
    }

    // 创建消息时使用。
    public Message(Project project, User user, String content, MessageType type) {
        this.project = project;
        this.user = user;
        this.content = content;
        this.type = type;
        this.createdAt = Instant.now();
    }

    public Long getId() { return id; }
    public Project getProject() { return project; }
    public User getUser() { return user; }
    public String getContent() { return content; }
    public MessageType getType() { return type; }
    public Instant getCreatedAt() { return createdAt; }
}
