package com.example.storyapp.viewmodel

import androidx.lifecycle.ViewModel
import com.example.storyapp.data.model.Story

class StoryDetailViewModel : ViewModel() {
   lateinit var listStory: Story

   fun setDetailStory(story: Story) : Story {
       listStory = story

       return listStory
   }

}