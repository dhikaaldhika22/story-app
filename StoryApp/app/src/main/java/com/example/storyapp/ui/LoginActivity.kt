package com.example.storyapp.ui

import android.animation.ObjectAnimator
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.view.WindowInsets
import android.view.WindowManager
import androidx.activity.viewModels
import androidx.appcompat.app.AlertDialog
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.preferencesDataStore
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import com.example.storyapp.R
import com.example.storyapp.api.ApiConfig
import com.example.storyapp.data.model.UserModel
import com.example.storyapp.viewmodel.LoginViewModel
import com.example.storyapp.databinding.ActivityLoginBinding
import com.example.storyapp.data.model.UserPreferences
import com.example.storyapp.data.repository.StoryRepository
import com.example.storyapp.data.result.ResultResponse
import com.example.storyapp.data.room.StoryDatabase
import com.example.storyapp.viewmodel.UserPreferencesModelFactory
import com.example.storyapp.viewmodel.ViewModelFactory

private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "settings")

class LoginActivity : AppCompatActivity() {
    private lateinit var binding: ActivityLoginBinding
    private val viewModel: LoginViewModel by viewModels {
        ViewModelFactory.getInstance(this)
    }
    private lateinit var loginViewModel: LoginViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        initAction()
    }

    private fun initAction() {
        setupView()
        setupViewModel()
        editTextListener()
        buttonAction()
        playAnimation()
    }

    private fun setupView() {
        @Suppress("DEPRECATION")
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            window.insetsController?.hide(WindowInsets.Type.statusBars())
        } else {
            window.setFlags(
                WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN
            )
        }
        supportActionBar?.hide()
    }

    private fun setupViewModel() {
        loginViewModel = ViewModelProvider(this, UserPreferencesModelFactory(UserPreferences.getInstance(dataStore),storyRepository = StoryRepository(
            StoryDatabase.getInstance(applicationContext), ApiConfig.getApiService())
        ))[LoginViewModel::class.java]
    }

    private fun editTextListener() {
        binding.edtEmail.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {

            }

            override fun onTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {

            }

            override fun afterTextChanged(s: Editable?) {

            }
        })

        binding.edtPassword.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {

            }

            override fun onTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {

            }

            override fun afterTextChanged(s: Editable?) {

            }
        })

        binding.daftar.setOnClickListener {
            startActivity(Intent(this@LoginActivity, RegistActivity::class.java))
        }
    }

    private fun showAlertDialog(param: Boolean, message: String) {
        if (param) {
            AlertDialog.Builder(this).apply {
                setTitle(getString(R.string.information))
                setMessage(getString(R.string.login_success))
                setPositiveButton(getString(R.string.continue_)) { _, _ ->
                    val intent = Intent(context, MainActivity::class.java)
                    intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK
                    startActivity(intent)
                    finish()
                }
                create()
                show()
            }
        } else {
            AlertDialog.Builder(this).apply {
                setTitle(getString(R.string.information))
                setMessage(getString(R.string.login_failed) +", $message")
                setPositiveButton(getString(R.string.continue_)) { _, _ ->
                    binding.pbLoading.visibility = View.GONE
                }
                create()
                show()

            }
        }
    }

    private fun buttonAction() {
        binding.btnLogin.setOnClickListener {
            val email = binding.edtEmail.text.toString()
            val pass = binding.edtPassword.text.toString()

            when {
                email.isEmpty() -> {
                    binding.edtEmail.error = getString(R.string.must_fill)
                }
                pass.isEmpty() -> {
                    binding.edtPassword.error = getString(R.string.must_fill)
                }
                else -> {
                    viewModel.login(email, pass).observe(this) {
                    when (it) {
                        is ResultResponse.Loading -> {
                            binding.pbLoading.visibility = View.VISIBLE
                        }
                        is ResultResponse.Success -> {
                            binding.pbLoading.visibility = View.GONE
                            val user = UserModel(
                                it.data.name,
                                email,
                                pass,
                                it.data.userId,
                                it.data.token,
                                true
                            )
                            showAlertDialog(true, "Login Berhasil")

                            val userAuth = UserPreferences.getInstance(dataStore)
                            lifecycleScope.launchWhenStarted {
                                userAuth.saveUser(user)
                            }
                        }
                        is ResultResponse.Error -> {
                            binding.pbLoading.visibility = View.GONE
                            showAlertDialog(false, it.error)
                        }
                    }
                    }
                }
            }
        }
    }

    private fun playAnimation() {
        ObjectAnimator.ofFloat(binding.ivWelcome, View.TRANSLATION_X, -30f, 30f).apply {
            duration = 6000
            repeatCount = ObjectAnimator.INFINITE
            repeatMode = ObjectAnimator.REVERSE
        }.start()
    }
}