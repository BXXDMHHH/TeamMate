package com.teammate.android.data

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object ApiClient {
    // Android Emulator -> host machine.
    // If the Spring Boot server is exposed by GitHub Codespaces, replace this
    // with the forwarded HTTPS URL and keep the trailing slash.
    private const val BASE_URL = "http://10.0.2.2:8080/"

    val api: TeamMateApi by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(TeamMateApi::class.java)
    }
}
