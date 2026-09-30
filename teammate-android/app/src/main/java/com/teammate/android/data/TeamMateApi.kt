package com.teammate.android.data

import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path
import retrofit2.http.Query

// 这个接口描述“Android 如何调用 Java Spring Boot 后端”。
// Retrofit 会根据下面的注解，自动生成真正的 HTTP 请求代码。
interface TeamMateApi {

    // 登录
    // HTTP: POST /api/auth/login
    // Body: { "email": "...", "password": "..." }
    @POST("api/auth/login")
    suspend fun login(@Body request: LoginRequest): LoginResponse

    // 获取当前用户加入的项目
    // HTTP: GET /api/projects?userId=1
    @GET("api/projects")
    suspend fun getProjects(
        @Query("userId") userId: Long
    ): List<Project>

    // 获取某个项目的聊天记录
    // HTTP: GET /api/projects/1/messages?userId=1
    @GET("api/projects/{projectId}/messages")
    suspend fun getMessages(
        @Path("projectId") projectId: Long,
        @Query("userId") userId: Long
    ): List<Message>

    // 发送一条聊天消息
    // HTTP: POST /api/projects/1/messages
    // Body: { "userId": 1, "content": "你好" }
    @POST("api/projects/{projectId}/messages")
    suspend fun sendMessage(
        @Path("projectId") projectId: Long,
        @Body request: SendMessageRequest
    ): Message
}
