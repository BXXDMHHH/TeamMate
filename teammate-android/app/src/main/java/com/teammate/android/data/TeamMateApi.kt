package com.teammate.android.data

import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path
import retrofit2.http.Query

interface TeamMateApi {
    @POST("api/auth/login")
    suspend fun login(@Body request: LoginRequest): LoginResponse

    @GET("api/projects")
    suspend fun getProjects(@Query("userId") userId: Long): List<Project>

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
}
