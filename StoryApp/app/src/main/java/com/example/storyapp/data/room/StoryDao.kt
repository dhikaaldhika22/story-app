package com.example.storyapp.data.room;

import androidx.paging.PagingSource
import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

import com.example.storyapp.data.model.Story;

@Dao
interface StoryDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertStory(storyEntity: List<Story>)

    @Query("SELECT * FROM story")
    fun getStory(): PagingSource<Int, Story>

    @Query("DELETE FROM story")
    suspend fun deleteAll()
}