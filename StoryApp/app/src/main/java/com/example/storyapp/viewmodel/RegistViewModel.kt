package com.example.storyapp.viewmodel

import androidx.lifecycle.ViewModel
import com.example.storyapp.data.repository.StoryRepository


class RegistViewModel(private val storyRepository: StoryRepository) : ViewModel() {
    fun register(name: String, email: String, password: String) = storyRepository.register(name, email, password)
}