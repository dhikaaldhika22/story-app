package com.example.storyapp.ui

import android.animation.ObjectAnimator
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
import com.example.storyapp.R
import com.example.storyapp.data.result.ResultResponse
import com.example.storyapp.viewmodel.RegistViewModel
import com.example.storyapp.databinding.ActivityRegistBinding
import com.example.storyapp.viewmodel.ViewModelFactory

class RegistActivity : AppCompatActivity() {
    private lateinit var binding: ActivityRegistBinding
    private val registViewModel: RegistViewModel by viewModels {
        ViewModelFactory.getInstance(this)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRegistBinding.inflate(layoutInflater)
        setContentView(binding.root)

        initAction()

    }

    private fun initAction() {
        setupView()
        editTextAction()
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

    private fun editTextAction() {
        binding.edtName.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {

            }

            override fun onTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {

            }

            override fun afterTextChanged(p0: Editable?) {

            }

        })

        binding.edtEmail.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {

            }

            override fun onTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {

            }

            override fun afterTextChanged(p0: Editable?) {

            }

        })

        binding.edtPassword.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {

            }

            override fun onTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {

            }

            override fun afterTextChanged(p0: Editable?) {

            }

        })

        binding.login.setOnClickListener {
            startActivity(Intent(this@RegistActivity, LoginActivity::class.java))
        }
    }

    private fun buttonAction() {
        binding.btnRegist.setOnClickListener {
            val name = binding.edtName.text.toString()
            val email = binding.edtEmail.text.toString()
            val password = binding.edtPassword.text.toString()
            when {
                name.isEmpty() -> {
                    binding.edtName.error = getString(R.string.must_fill)
                }
                email.isEmpty() -> {
                    binding.edtEmail.error = getString(R.string.must_fill)
                }
                password.isEmpty() -> {
                    binding.edtPassword.error = getString(R.string.must_fill)
                }
                else -> {
                    registViewModel.register(name, email, password).observe(this) {
                        when (it) {
                            is ResultResponse.Loading -> {
                                binding.pbLoading.visibility = View.VISIBLE
                            }
                            is ResultResponse.Success -> {
                                binding.pbLoading.visibility = View.GONE
                                showAlertDialog(true, getString(R.string.regist_success))
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

    private fun showAlertDialog(param: Boolean, message: String) {
        if (param) {
            AlertDialog.Builder(this).apply {
                setTitle(getString(R.string.information))
                setMessage(getString(R.string.regist_success))
                setPositiveButton(getString(R.string.continue_)) { _, _ ->
                    val intent = Intent(context, LoginActivity::class.java)
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
                setMessage(getString(R.string.regist_failed) +", $message")
                setPositiveButton(getString(R.string.continue_)) { _, _ ->
                    binding.pbLoading.visibility = View.GONE
                }
                create()
                show()

            }
        }
    }

    private fun playAnimation() {
        ObjectAnimator.ofFloat(binding.ivRegister, View.TRANSLATION_X, -30f, 30f).apply {
            duration = 6000
            repeatCount = ObjectAnimator.INFINITE
            repeatMode = ObjectAnimator.REVERSE
        }.start()
    }
}


