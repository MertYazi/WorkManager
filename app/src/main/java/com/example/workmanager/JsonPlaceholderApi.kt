package com.example.workmanager

import retrofit2.Response
import retrofit2.http.GET

interface JsonPlaceholderApi {

    @GET("posts/1")
    suspend fun getPost(): Response<Post>
}