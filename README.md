# TeamMate

TeamMate 是一个面向小型研发团队的 AI 项目协作 Demo。

## V1 目标

- 用户登录
- 查看项目
- 进入项目聊天室
- 发送/查看聊天消息
- AI 总结讨论
- AI 提取待办事项
- AI 根据项目聊天记录回答问题

## 技术栈

### Android

- Kotlin
- Jetpack Compose

### Backend

- Java 21
- Spring Boot
- Spring Web
- Spring Data JPA
- H2（V1 开发阶段）

## Repository Structure

```text
TeamMate/
├── teammate-server/      # Spring Boot backend
├── teammate-android/     # Android client
└── README.md
```

## Development Plan

1. 搭建 Spring Boot 后端
2. 建立 User / Project / Message 数据模型
3. 完成登录和项目 API
4. 完成 Android 基础页面
5. 完成聊天功能
6. 接入 AI
7. 实现 Summary / Tasks / Ask

当前为 Step 1：项目骨架搭建。