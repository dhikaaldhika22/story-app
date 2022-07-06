package com.example.storyapp.data.repository

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.liveData
import androidx.paging.ExperimentalPagingApi
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import com.example.storyapp.api.ApiService
import com.example.storyapp.data.model.*
import com.example.storyapp.data.result.ResultResponse
import com.example.storyapp.data.room.StoryDatabase
import com.example.storyapp.helper.wrapEspressoIdlingResource
import kotlinx.coroutines.flow.Flow
import okhttp3.MultipartBody
import okhttp3.RequestBody
import java.lang.Exception

class StoryRepository(private val storyDatabase: StoryDatabase, private val apiService: ApiService) {
    fun register(name: String, email: String, password: String): LiveData<ResultResponse<LoginResponse>> =
        liveData {
            emit(ResultResponse.Loading)

            try {
                val response = apiService.register(name, email, password)
                if (!response.error) {
                    emit(ResultResponse.Success(response))
                } else {
                    Log.e(TAG, "Failed: ${response.message}")
                    emit(ResultResponse.Error(response.message))
                }
            } catch (e: Exception) {
                Log.e(TAG, "Exception: ${e.message.toString()} ")
                emit(ResultResponse.Error(e.message.toString()))
            }
        }

    fun login(email: String, pass: String): LiveData<ResultResponse<LoginResult>> =
        liveData {
            emit(ResultResponse.Loading)

            try {
                val response = apiService.login(email, pass)
                if (!response.error) {
                    emit(ResultResponse.Success(response.loginResult))
                } else {
                    Log.e(TAG, "Failed: ${response.message}")
                    emit(ResultResponse.Error(response.message))
                }
            } catch (e: Exception) {
                Log.e(TAG, "Exception: ${e.message.toString()} ")
                emit(ResultResponse.Error(e.message.toString()))
            }
        }

    fun getStoryMap(token: String): LiveData<ResultResponse<List<Story>>> =
        liveData {
            emit(ResultResponse.Loading)

            try {
                val response = apiService.getStoriesLocation("Bearer $token")
                if (!response.error) {
                    emit(ResultResponse.Success(response.listStory))
                } else {
                    Log.e(TAG, "Failed: ${response.message}")
                    emit(ResultResponse.Error(response.message))
                }

            } catch (e: Exception) {
                Log.e(TAG, "Exception: ${e.message.toString()} ")
                emit(ResultResponse.Error(e.message.toString()))
            }
        }

    fun upload(
        token: String,
        description: RequestBody,
        imageMultipart: MultipartBody.Part,
        lat: RequestBody? = null,
        lon: RequestBody? = null
    ): LiveData<ResultResponse<FileUploadResponse>> = liveData {
        emit(ResultResponse.Loading)

        try {
            val response = apiService.uploadImage("Bearer $token", imageMultipart, description, lat, lon)
            if (!response.error) {
                emit(ResultResponse.Success(response))
            } else {
                Log.e(TAG, "Failed: ${response.message}")
                emit(ResultResponse.Error(response.message))
            }
        } catch (e: Exception) {
            Log.e(TAG, "Exception: ${e.message.toString()} ")
            emit(ResultResponse.Error(e.message.toString()))
        }
    }

    fun getPagingStories(token: String): Flow<PagingData<Story>> {
        wrapEspressoIdlingResource {
            @OptIn(ExperimentalPagingApi::class)
            return Pager(
                config = PagingConfig(pageSize = 5),
                remoteMediator = RemoteMediatorStory(storyDatabase, apiService, token),
                pagingSourceFactory = {
                    storyDatabase.storyDao().getStory()
                }
            ).flow
        }
    }

    companion object {
        private const val TAG = "StoryRepository"
    }
}