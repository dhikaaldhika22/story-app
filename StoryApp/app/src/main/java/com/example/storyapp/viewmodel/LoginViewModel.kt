package com.example.storyapp.viewmodel

import androidx.lifecycle.*
import com.example.storyapp.data.repository.StoryRepository

class LoginViewModel(private val storyRepository: StoryRepository) : ViewModel() {
    fun login(email: String, password: String) = storyRepository.login(email, password)
}