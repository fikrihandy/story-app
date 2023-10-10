package academy.bangkit.storyapp.view.authenticatiton.login

import academy.bangkit.storyapp.R
import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import academy.bangkit.storyapp.data.pref.UserModel
import academy.bangkit.storyapp.databinding.ActivityLoginBinding
import academy.bangkit.storyapp.view.ViewModelFactory
import academy.bangkit.storyapp.view.liststory.MainActivity
import academy.bangkit.storyapp.view.authenticatiton.signup.SignupActivity
import academy.bangkit.storyapp.view.extension.EnableFullscreen
import android.text.Editable
import android.text.TextWatcher
import com.google.android.material.textfield.TextInputLayout

class LoginActivity : AppCompatActivity() {
    private val viewModel by viewModels<LoginViewModel> {
        ViewModelFactory.getInstance(this)
    }
    private lateinit var binding: ActivityLoginBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupCustomView()
        setLoginButton()

        EnableFullscreen.setupView(window, supportActionBar)
        setupAction()
        playAnimation()
    }

    private fun setupCustomView() {
        binding.edLoginEmail.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
                val s2 = s.toString()
                if (s2.isEmpty()) {
                    binding.edLoginEmail.error = "Email is required"
                } else {
                    binding.edLoginEmail.error = null
                }
                setLoginButton()
            }

            override fun afterTextChanged(s: Editable) {}
        })

        binding.edLoginPassword.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
                val s3 = s.toString()
                if (s3.length < 8) {
                    binding.edLoginPassword.error = "Min 8 char!!"
                    binding.passwordEditTextLayout.endIconMode = TextInputLayout.END_ICON_NONE
                } else {
                    binding.edLoginPassword.error = null
                    binding.passwordEditTextLayout.endIconMode =
                        TextInputLayout.END_ICON_PASSWORD_TOGGLE
                }
                setLoginButton()
            }

            override fun afterTextChanged(s: Editable) {}
        })
    }

    fun setLoginButton() {
        val email = binding.edLoginEmail.text.toString()
        val password = binding.edLoginPassword.text.toString()

        binding.loginButton.isEnabled =
            email.isNotEmpty() && password.length >= 8
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