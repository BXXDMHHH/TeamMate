package com.teammate.android.data

data class LoginRequest(val email: String, val password: String)
data class LoginResponse(val userId: Long, val username: String, val email: String)
data class Project(val id: Long, val name: String, val description: String?, val createdAt: String)
data class Message(
    val id: Long,
    val userId: Long?,
    val username: String,
    val content: String,
    val type: String,
    val createdAt: String
)
data class SendMessageRequest(val userId: Long, val content: String)
