package com.example.storyapp.viewmodel

import androidx.lifecycle.ViewModel
import com.example.storyapp.data.model.UserModel
import com.example.storyapp.data.repository.StoryRepository
import okhttp3.MultipartBody
import okhttp3.RequestBody

class AddStoryViewModel(private val storyRepository: StoryRepository) : ViewModel() {
    fun uploadImage(
        token: String,
        description: RequestBody,
        imageMultipart: MultipartBody.Part,
        lat: RequestBody? = null,
        lon: RequestBody? = null
    ) = storyRepository.upload(token, description, imageMultipart, lat, lon)
}