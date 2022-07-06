package com.example.storyapp.viewmodel

import androidx.lifecycle.*
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.example.storyapp.data.model.Story
import com.example.storyapp.data.model.UserModel
import com.example.storyapp.data.model.UserPreferences
import com.example.storyapp.data.repository.StoryRepository
import kotlinx.coroutines.launch

class MainViewModel(private val pref: UserPreferences, private val storyRepository: StoryRepository) : ViewModel() {
    fun showStories(token: String) : LiveData<PagingData<Story>> {
        return storyRepository.getPagingStories(token).cachedIn(viewModelScope).asLiveData()
    }

    fun getUser(): LiveData<UserModel> {
        return pref.getUser().asLiveData()
    }

    fun logout() {
        viewModelScope.launch {
            pref.logout()
        }
    }
}