package com.teammate.android.data

// -----------------------------
// 登录相关的数据结构
// -----------------------------

// Android -> Java 后端：登录时发送的数据。
data class LoginRequest(
    val email: String,
    val password: String
)

// Java 后端 -> Android：登录成功后返回的数据。
data class LoginResponse(
    val userId: Long,
    val username: String,
    val email: String
)

// -----------------------------
// 项目数据
// -----------------------------

// 一个 Project 对应 Java 后端里的 Project。
data class Project(
    val id: Long,
    val name: String,
    val description: String?,
    val createdAt: String
)

// -----------------------------
// 聊天消息
// -----------------------------

// 对应 Java 后端里的 MessageResponse。
// 一条消息可能来自普通用户，也可能来自以后加入的 AI。
data class Message(
    val id: Long,
    val userId: Long?,
    val username: String,
    val content: String,
    val type: String,
    val createdAt: String
)

// Android -> Java 后端：发送消息时提交的数据。
data class SendMessageRequest(
    val userId: Long,
    val content: String
)
