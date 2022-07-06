package com.example.storyapp.api

import com.example.storyapp.data.model.*
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.http.*

interface ApiService {
    @FormUrlEncoded
    @POST("register")
    suspend fun register(
        @Field("name") name: String,
        @Field("email") email: String,
        @Field("password") password: String
    ): LoginResponse

    @FormUrlEncoded
    @POST("login")
    suspend fun login(
        @Field("email") email: String,
        @Field("password") password: String
    ) : LoginResponse

    @GET("stories")
    suspend fun getAllStories(
        @Header("Authorization") token: String,
        @Query("page") page: Int,
        @Query("size") size: Int
    ): StoryResponse

    @Multipart
    @POST("stories")
     suspend fun uploadImage(
        @Header("Authorization") token: String,
        @Part file:MultipartBody.Part,
        @Part("description") description: RequestBody,
        @Part("lat") latitude:  RequestBody?,
        @Part("lon") longitude: RequestBody?
    ): FileUploadResponse

     @GET("stories?location=1")
     suspend fun getStoriesLocation(
         @Header("Authorization") token: String
     ): StoryResponse
}