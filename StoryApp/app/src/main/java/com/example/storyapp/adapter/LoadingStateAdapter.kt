package com.example.storyapp.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.paging.LoadState
import androidx.paging.LoadStateAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.storyapp.databinding.LoadingItemBinding

class LoadingStateAdapter(private val retry: () -> Unit) : LoadStateAdapter<LoadingStateAdapter.LoadingViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, loadState: LoadState): LoadingViewHolder {
        val binding = LoadingItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)

        return LoadingViewHolder(binding, retry)
    }

    override fun onBindViewHolder(holder: LoadingViewHolder, loadState: LoadState) {
        holder.bind(loadState)
    }

    class LoadingViewHolder(private val binding: LoadingItemBinding, retry: () -> Unit) : RecyclerView.ViewHolder(binding.root) {
        init {
            binding.btnRetry.setOnClickListener { retry.invoke() }
        }

        fun bind(loadState: LoadState) {
            if (loadState is LoadState.Error) {
                binding.tvErrorMsg.text = loadState.error.localizedMessage
            }
            binding.pbLoading.isVisible = loadState is LoadState.Loading
            binding.tvErrorMsg.isVisible = loadState is LoadState.Error
            binding.btnRetry.isVisible = loadState is LoadState.Error
        }
    }
}