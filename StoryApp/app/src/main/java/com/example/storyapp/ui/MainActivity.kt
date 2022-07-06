package com.example.storyapp.ui

import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import androidx.activity.viewModels
import com.example.storyapp.databinding.ActivityMainBinding
import androidx.appcompat.app.AlertDialog
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.preferencesDataStore
import androidx.lifecycle.ViewModelProvider
import androidx.core.view.isVisible
import androidx.lifecycle.lifecycleScope
import androidx.paging.LoadState
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.storyapp.R
import com.example.storyapp.adapter.LoadingStateAdapter
import com.example.storyapp.viewmodel.MainViewModel
import com.example.storyapp.adapter.StoryAdapter
import com.example.storyapp.api.ApiConfig
import com.example.storyapp.data.model.UserModel
import com.example.storyapp.data.model.UserPreferences
import com.example.storyapp.data.repository.StoryRepository
import com.example.storyapp.data.room.StoryDatabase
import com.example.storyapp.viewmodel.UserPreferencesModelFactory
import com.example.storyapp.viewmodel.ViewModelFactory
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

private val Context.dataStore: DataStore<Preferences> by preferencesDataStore("settings")

class MainActivity : AppCompatActivity() {

    private var _binding: ActivityMainBinding? = null
    private val binding get() = _binding
    private val viewModel: MainViewModel by viewModels {
        ViewModelFactory.getInstance(this)
    }
    private lateinit var mainViewModel: MainViewModel
    private lateinit var adapter: StoryAdapter
    private lateinit var user: UserModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        _binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding?.root)

        initAction()
    }

    private fun initAction() {
        setupViewModel()
        listStoryAdapter()
        toAddStory()
        swipeToRefresh()
    }

    private fun setupViewModel() {
        mainViewModel = ViewModelProvider(
            this,
            UserPreferencesModelFactory(UserPreferences.getInstance(dataStore), storyRepository = StoryRepository(
                StoryDatabase.getInstance(applicationContext),ApiConfig.getApiService()))
        )[MainViewModel::class.java]

        mainViewModel.getUser().observe(this) {
            user = UserModel(
                it.name,
                it.email,
                it.password,
                it.userId,
                it.token,
                true
            )

            viewModel.showStories(user.token).observe(this) {
                adapter.submitData(lifecycle, it)
            }
        }
    }

    private fun listStoryAdapter() {
        adapter = StoryAdapter()
        binding?.rvStories?.adapter = adapter.withLoadStateHeaderAndFooter(
            footer = LoadingStateAdapter { adapter.retry() },
            header = LoadingStateAdapter { adapter.retry() }
        )
        binding?.rvStories?.layoutManager = LinearLayoutManager(this)
        binding?.rvStories?.setHasFixedSize(true)

        lifecycleScope.launchWhenCreated {
            adapter.loadStateFlow.collect {
                binding?.swipeRefresh?.isRefreshing = it.mediator?.refresh is LoadState.Loading
            }
        }
        lifecycleScope.launch {
            adapter.loadStateFlow.collectLatest { loadStates ->
                binding?.errorView?.root?.isVisible = loadStates.refresh is LoadState.Error
            }
            if (adapter.itemCount < 1) binding?.errorView?.root?.visibility = View.VISIBLE
            else binding?.errorView?.root?.visibility = View.GONE
        }
    }

    private fun swipeToRefresh() {
        binding?.swipeRefresh?.setOnRefreshListener {
            adapter.refresh()
        }
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        val inflater = menuInflater
        inflater.inflate(R.menu.menu, menu)


        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.logout -> {
                mainViewModel.logout()
                AlertDialog.Builder(this).apply {
                    setTitle(getString(R.string.information))
                    setMessage(getString(R.string.logout_success))
                    setPositiveButton(getString(R.string.continue_)) { _, _ ->
                        Intent(this@MainActivity, LoginActivity::class.java).also {
                            startActivity(it)
                        }
                    }
                    create()
                    show()
                }
            }
            R.id.maps -> {
                val intent = Intent(this@MainActivity, MapsActivity::class.java)
                intent.putExtra(EXTRA_USER, user)
                startActivity(intent)
            }
        }
        return super.onOptionsItemSelected(item)
    }

    private fun toAddStory() {
        binding?.fabAdd?.setOnClickListener {
            val intent = Intent(this@MainActivity, AddStoryActivity::class.java)
            intent.putExtra(EXTRA_USER, user)
            startActivity(intent)
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }

    override fun onResume() {
        super.onResume()

        setupViewModel()
    }

    companion object {
        const val EXTRA_USER = "user"
    }
}