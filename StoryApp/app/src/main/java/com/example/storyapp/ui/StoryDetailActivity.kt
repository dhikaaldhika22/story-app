package com.example.storyapp.ui

import android.graphics.text.LineBreaker.JUSTIFICATION_MODE_INTER_WORD
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.activity.viewModels
import androidx.annotation.RequiresApi
import com.bumptech.glide.Glide
import com.example.storyapp.R
import com.example.storyapp.viewmodel.StoryDetailViewModel
import com.example.storyapp.databinding.ActivityStoryDetailBinding
import com.example.storyapp.data.model.Story
import com.example.storyapp.helper.Helper
import java.util.*

class StoryDetailActivity : AppCompatActivity() {
    private lateinit var binding: ActivityStoryDetailBinding
    private lateinit var story: Story

    private val storyDetailViewModel by viewModels<StoryDetailViewModel>()

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityStoryDetailBinding.inflate(layoutInflater)
        setContentView(binding.root)

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            run {
                binding.tvStoryDesc.justificationMode = JUSTIFICATION_MODE_INTER_WORD
            }
        }

        story = intent.getParcelableExtra(EXTRA_STORY)!!
        storyDetailViewModel.setDetailStory(story)
        parseDetail()
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun parseDetail() {
        with(binding) {
            tvStoryAuthor.text = storyDetailViewModel.listStory.name
            tvStoryDesc.text = storyDetailViewModel.listStory.description
            Glide.with(ivStoryThumbnail)
                .load(storyDetailViewModel.listStory.photoUrl)
                .placeholder(R.drawable.ic_place_holder)
                .into(ivStoryThumbnail)
            tvStoryCreated.text = binding.root.resources.getString(R.string.created_at, Helper.formatDate(storyDetailViewModel.listStory.createdAt,
                TimeZone.getDefault().id ))
        }
    }

    companion object {
        const val EXTRA_STORY = "story"
    }
}