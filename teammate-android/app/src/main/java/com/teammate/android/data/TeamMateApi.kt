package com.teammate.android.data

import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path
import retrofit2.http.Query

// 这个接口描述“Android 如何调用 Java Spring Boot 后端”。
interface TeamMateApi {

    @POST("api/auth/login")
    suspend fun login(@Body request: LoginRequest): LoginResponse

    @GET("api/projects")
    suspend fun getProjects(
        @Query("userId") userId: Long
    ): List<Project>

    @GET("api/projects/{projectId}/messages")
    suspend fun getMessages(
        @Path("projectId") projectId: Long,
        @Query("userId") userId: Long
    ): List<Message>

    @POST("api/projects/{projectId}/messages")
    suspend fun sendMessage(
        @Path("projectId") projectId: Long,
        @Body request: SendMessageRequest
    ): Message

    // AI：总结最近的项目讨论
    @POST("api/projects/{projectId}/ai/summarize")
    suspend fun summarize(
        @Path("projectId") projectId: Long,
        @Query("userId") userId: Long
    ): Message

    // AI：从聊天记录中提取待办任务
    @POST("api/projects/{projectId}/ai/tasks")
    suspend fun extractTasks(
        @Path("projectId") projectId: Long,
        @Query("userId") userId: Long
    ): Message

    // AI：根据项目聊天记录回答用户问题
    @POST("api/projects/{projectId}/ai/ask")
    suspend fun askAI(
        @Path("projectId") projectId: Long,
        @Query("userId") userId: Long,
        @Body request: AIAskRequest
    ): Message
}
