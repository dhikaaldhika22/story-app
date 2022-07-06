package com.example.storyapp.di

import android.content.Context
import com.example.storyapp.api.ApiConfig
import com.example.storyapp.data.repository.StoryRepository
import com.example.storyapp.data.room.StoryDatabase

object Injection {
    fun providerStoryRepo(context: Context): StoryRepository {
        val db = StoryDatabase.getInstance(context)
        val apiService = ApiConfig.getApiService()

        return StoryRepository(db, apiService)
    }
}