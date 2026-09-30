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
- Spring Boot 3.5.6
- Spring Web
- Spring Data JPA
- H2（V1 开发阶段）
- BCrypt（密码哈希）

## Repository Structure

```text
TeamMate/
├── teammate-server/      # Spring Boot backend
├── teammate-android/     # Android client
└── README.md
```

## Backend Progress

### Step 1 — Project skeleton

- Spring Boot 项目
- Health API
- H2 配置

### Step 2 — Core backend

- User / Project / ProjectMember / Message Entity
- Repository
- Auth / Project / Message Service
- Login / Project / Message Controller
- Demo seed data

## Demo Accounts

```text
Alice
email: alice@test.com
password: 123456

Bob
email: bob@test.com
password: 123456

Charlie
email: charlie@test.com
password: 123456
```

## API

```text
GET  /api/health
POST /api/auth/login
GET  /api/projects?userId=1
GET  /api/projects/{projectId}?userId=1
GET  /api/projects/{projectId}/messages?userId=1
POST /api/projects/{projectId}/messages
```

### Login example

```json
POST /api/auth/login
{
  "email": "alice@test.com",
  "password": "123456"
}
```

### Send message example

```json
POST /api/projects/1/messages
{
  "userId": 1,
  "content": "测试一下 TeamMate 聊天功能"
}
```

## Development Plan

1. 搭建 Spring Boot 后端 ✅
2. 建立 User / Project / Message 数据模型 ✅
3. 完成登录和项目/消息 API ✅
4. 完成 Android 基础页面
5. 完成聊天功能
6. 接入 AI
7. 实现 Summary / Tasks / Ask

当前进入 Step 3：先验证后端 API，再开始 Android 客户端。
