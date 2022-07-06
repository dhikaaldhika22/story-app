package com.example.storyapp.viewmodel

import androidx.lifecycle.ViewModel
import com.example.storyapp.data.repository.StoryRepository

class MapsViewModel(private val storyRepository: StoryRepository) : ViewModel() {
    fun getStories(token: String) = storyRepository.getStoryMap(token)
}