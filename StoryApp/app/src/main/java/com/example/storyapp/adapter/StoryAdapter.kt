package com.example.storyapp.adapter

import android.app.Activity
import android.content.Intent
import android.os.Build
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.annotation.RequiresApi
import androidx.core.app.ActivityOptionsCompat
import androidx.core.util.Pair
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.example.storyapp.data.model.Story
import com.bumptech.glide.Glide
import com.example.storyapp.R
import com.example.storyapp.databinding.StoryListBinding
import com.example.storyapp.helper.Helper
import com.example.storyapp.ui.StoryDetailActivity
import java.util.*

class StoryAdapter : PagingDataAdapter<Story, StoryAdapter.ListViewHolder>(DIFF_CALLBACK) {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ListViewHolder {
        val binding = StoryListBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ListViewHolder(binding)
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onBindViewHolder(holder: ListViewHolder, position: Int) {
        val data = getItem(position)
        if (data != null) holder.bind(data)
    }

    class ListViewHolder(private var binding: StoryListBinding) : RecyclerView.ViewHolder(binding.root) {
        @RequiresApi(Build.VERSION_CODES.O)
        fun bind(story: Story) {
            with(binding) {
                Glide.with(ivStoryThumbnail.context)
                    .load(story.photoUrl)
                    .centerCrop()
                    .into(ivStoryThumbnail)
                tvStoryAuthor.text = story.name
                tvStoryDesc.text = story.description
                tvStoryCreated.text = binding.root.resources.getString(R.string.created_at, Helper.formatDate(story.createdAt, TimeZone.getDefault().id))

                itemView.setOnClickListener {
                    val optionsCompat: ActivityOptionsCompat = ActivityOptionsCompat.makeSceneTransitionAnimation(
                        itemView.context as Activity,
                        Pair(ivStoryThumbnail, "image"),
                        Pair(tvStoryAuthor, "name"),
                        Pair(tvStoryDesc, "description"),
                        Pair(tvStoryCreated, "created")
                    )

                    val intent = Intent(itemView.context, StoryDetailActivity::class.java)
                    intent.putExtra(StoryDetailActivity.EXTRA_STORY, story)

                    itemView.context.startActivity(intent, optionsCompat.toBundle())
                }
            }
        }
    }

    companion object {
        val DIFF_CALLBACK = object : DiffUtil.ItemCallback<Story>() {
            override fun areItemsTheSame(oldItem: Story, newItem: Story): Boolean {
                return oldItem == newItem
            }

            override fun areContentsTheSame(oldItem: Story, newItem: Story): Boolean {
                return oldItem.id == newItem.id
            }

        }
    }
}