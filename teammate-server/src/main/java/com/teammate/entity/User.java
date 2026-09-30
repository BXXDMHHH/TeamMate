package com.teammate.entity;

import jakarta.persistence.*;

import java.time.Instant;

// @Entity：告诉 JPA，这个 Java 类需要映射成数据库中的一张表。
@Entity
// @Table：指定数据库表名为 users。
@Table(name = "users")
public class User {

    // @Id：主键。
    // @GeneratedValue：让数据库自动生成 id，例如 1、2、3……
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // 用户在 App 中显示的名字，例如 Alice。
    @Column(nullable = false, length = 50)
    private String username;

    // 登录邮箱。
    // unique = true：不允许两个用户使用相同邮箱。
    @Column(nullable = false, unique = true, length = 120)
    private String email;

    // 数据库中保存的是“加密后的密码”，不是用户输入的明文密码。
    @Column(name = "password_hash", nullable = false)
    private String passwordHash;

    // 用户创建时间。
    @Column(name = "created_at", nullable = false)
    private Instant createdAt;

    // JPA 需要一个无参数构造方法来创建对象。
    // protected 表示普通业务代码不应该直接调用它。
    protected User() {
    }

    // 创建一个新用户时使用这个构造方法。
    public User(String username, String email, String passwordHash) {
        this.username = username;
        this.email = email;
        this.passwordHash = passwordHash;
        this.createdAt = Instant.now();
    }

    public Long getId() { return id; }
    public String getUsername() { return username; }
    public String getEmail() { return email; }
    public String getPasswordHash() { return passwordHash; }
    public Instant getCreatedAt() { return createdAt; }
}
