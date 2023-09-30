package academy.bangkit.storyapp.view.authenticatiton.login

import academy.bangkit.storyapp.R
import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.view.View
import android.view.WindowInsets
import android.view.WindowManager
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import academy.bangkit.storyapp.data.pref.UserModel
import academy.bangkit.storyapp.databinding.ActivityLoginBinding
import academy.bangkit.storyapp.view.ViewModelFactory
import academy.bangkit.storyapp.view.authenticatiton.customview.CustomViewUtil
import academy.bangkit.storyapp.view.liststory.MainActivity
import academy.bangkit.storyapp.view.authenticatiton.signup.SignupActivity
import android.util.Log

class LoginActivity : AppCompatActivity() {
    private val viewModel by viewModels<LoginViewModel> {
        ViewModelFactory.getInstance(this)
    }
    private lateinit var binding: ActivityLoginBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        CustomViewUtil.setupCustomView(
            binding.edLoginEmail,
            binding.edLoginPassword,
            binding.loginButton,
            binding.passwordEditTextLayout
        )

        CustomViewUtil.setLoginButtonEnable(
            binding.edLoginEmail.text.toString(),
            binding.edLoginPassword.text.toString(),
            binding.loginButton
        )

        setupView()
        setupAction()
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

    private fun setupAction() {
        binding.messageTextView.setOnClickListener {
            startActivity(Intent(this, SignupActivity::class.java))
        }
        binding.loginButton.setOnClickListener {
            val email = binding.edLoginEmail.text.toString()
            val password = binding.edLoginPassword.text.toString()
            viewModel.postLogin(email, password)
            viewModel.loginResponseLiveData.observe(this) {
                viewModel.loginResponseLiveData.observe(this) {
                    if (!it.error) {
                        binding.whenFailed.visibility = View.INVISIBLE
                        if (it.loginResult?.token != null) {
                            viewModel.saveSession(UserModel(email, it.loginResult.token))
                        }
                        val intent = Intent(this@LoginActivity, MainActivity::class.java)
                        intent.flags =
                            Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK
                        startActivity(intent)
                        finish()
                    } else {
                        binding.whenFailed.apply {
                            text = resources.getString(R.string.login_error, it.message)
                            visibility = View.VISIBLE
                        }
                    }
                }

            }

            viewModel.isLoading.observe(this) { isLoading ->
                showLoading(isLoading)
            }

        }
    }

    private fun showLoading(isLoading: Boolean) {
        with(binding) {
            if (isLoading) whenFailed.visibility = View.GONE
            loadingView.progressBar.visibility = if (isLoading) View.VISIBLE else View.GONE
            edLoginEmail.isEnabled = !isLoading
            edLoginPassword.isEnabled = !isLoading
            loginButton.isEnabled = !isLoading
            passwordEditTextLayout.isEnabled = !isLoading
            emailEditTextLayout.isEnabled = !isLoading
            messageTextView.isEnabled = !isLoading
        }
    }

    private fun playAnimation() {
        ObjectAnimator.ofFloat(binding.imageView, View.TRANSLATION_X, -30f, 30f).apply {
            duration = 6000
            repeatCount = ObjectAnimator.INFINITE
            repeatMode = ObjectAnimator.REVERSE
        }.start()

        val title = ObjectAnimator.ofFloat(binding.titleTextView, View.ALPHA, 1f).setDuration(100)
        val message =
            ObjectAnimator.ofFloat(binding.messageTextView, View.ALPHA, 1f).setDuration(100)
        val emailTextView =
            ObjectAnimator.ofFloat(binding.emailTextView, View.ALPHA, 1f).setDuration(100)
        val emailEditTextLayout =
            ObjectAnimator.ofFloat(binding.emailEditTextLayout, View.ALPHA, 1f).setDuration(100)
        val passwordTextView =
            ObjectAnimator.ofFloat(binding.passwordTextView, View.ALPHA, 1f).setDuration(100)
        val passwordEditTextLayout =
            ObjectAnimator.ofFloat(binding.passwordEditTextLayout, View.ALPHA, 1f).setDuration(100)
        val login = ObjectAnimator.ofFloat(binding.loginButton, View.ALPHA, 1f).setDuration(100)

        AnimatorSet().apply {
            playSequentially(
                title,
                message,
                emailTextView,
                emailEditTextLayout,
                passwordTextView,
                passwordEditTextLayout,
                login
            )
            startDelay = 100
        }.start()
    }
}