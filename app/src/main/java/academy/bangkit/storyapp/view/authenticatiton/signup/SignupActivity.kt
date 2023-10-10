package academy.bangkit.storyapp.view.authenticatiton.signup

import academy.bangkit.storyapp.R
import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import academy.bangkit.storyapp.databinding.ActivitySignupBinding
import academy.bangkit.storyapp.view.ViewModelFactory
import academy.bangkit.storyapp.view.extension.EnableFullscreen
import android.text.Editable
import android.text.TextWatcher
import androidx.activity.viewModels
import com.google.android.material.textfield.TextInputLayout

class SignupActivity : AppCompatActivity() {
    private lateinit var binding: ActivitySignupBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySignupBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupCustomView()
        setRegisterButton()

        EnableFullscreen.setupView(window, supportActionBar)
        setupAction()
        playAnimation()
    }

    private fun setupAction() {
        binding.signupButton.setOnClickListener {

            val viewModel by viewModels<SignupViewModel> {
                ViewModelFactory.getInstance(this)
            }

            val name = binding.edRegisterName.text.toString()
            val email = binding.edRegisterEmail.text.toString()
            val password = binding.edRegisterPassword.text.toString()

            viewModel.postRegister(name, email, password)

            viewModel.isSuccessful.observe(this) {
                if (it) {
                    AlertDialog.Builder(this).apply {
                        setTitle("Yeah!")
                        setMessage("Account creation successful. Welcome to StoryApp!")
                        setCancelable(false)
                        setPositiveButton("Login") { _, _ ->
                            finish()
                        }
                        create()
                        show()
                        binding.whenFailed.visibility = View.INVISIBLE
                    }
                } else {
                    viewModel.registerResponse.observe(this) { registerResponse ->
                        binding.whenFailed.text =
                            resources.getString(R.string.sign_up_error, registerResponse.message)
                    }
                    binding.whenFailed.visibility = View.VISIBLE
                }
            }

            viewModel.isLoading.observe(this) {
                showLoading(it)
            }
        }
    }

    private fun setupCustomView() {
        binding.edRegisterName.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(
                s: CharSequence,
                start: Int,
                count: Int,
                after: Int
            ) {
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                val s1 = s.toString()
                if (s1.isEmpty()) {
                    binding.edRegisterName.error = "Name is required"
                } else {
                    binding.edRegisterName.error = null
                }
                setRegisterButton()
            }

            override fun afterTextChanged(s: Editable?) {}

        })

        binding.edRegisterEmail.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
                val s2 = s.toString()
                if (s2.isEmpty()) {
                    binding.edRegisterEmail.error = "Email is required"
                } else {
                    binding.edRegisterEmail.error = null
                }
                setRegisterButton()
            }

            override fun afterTextChanged(s: Editable) {}
        })

        binding.edRegisterPassword.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
                val s3 = s.toString()
                if (s3.length < 8) {
                    binding.edRegisterPassword.error = "Min 8 char!!"
                    binding.passwordEditTextLayout.endIconMode = TextInputLayout.END_ICON_NONE
                } else {
                    binding.edRegisterPassword.error = null
                    binding.passwordEditTextLayout.endIconMode =
                        TextInputLayout.END_ICON_PASSWORD_TOGGLE
                }
                setRegisterButton()
            }

            override fun afterTextChanged(s: Editable) {}
        })

    }

    fun setRegisterButton() {
        val name = binding.edRegisterName.text.toString()
        val email = binding.edRegisterEmail.text.toString()
        val password = binding.edRegisterPassword.text.toString()

        binding.signupButton.isEnabled =
            name.isNotEmpty() && email.isNotEmpty() && password.length >= 8
    }

    private fun showLoading(isLoading: Boolean) {
        with(binding) {
            if (isLoading) whenFailed.visibility = View.GONE
            loadingView.progressBar.visibility = if (isLoading) View.VISIBLE else View.GONE
            edRegisterName.isEnabled = !isLoading
            edRegisterEmail.isEnabled = !isLoading
            edRegisterPassword.isEnabled = !isLoading
            signupButton.isEnabled = !isLoading
            nameEditTextLayout.isEnabled = !isLoading
            emailEditTextLayout.isEnabled = !isLoading
            passwordEditTextLayout.isEnabled = !isLoading
        }
    }

    private fun playAnimation() {
        ObjectAnimator.ofFloat(binding.imageView, View.TRANSLATION_X, -30f, 30f).apply {
            duration = 6000
            repeatCount = ObjectAnimator.INFINITE
            repeatMode = ObjectAnimator.REVERSE
        }.start()

        val title = ObjectAnimator.ofFloat(binding.titleTextView, View.ALPHA, 1f).setDuration(100)
        val nameTextView =
            ObjectAnimator.ofFloat(binding.nameTextView, View.ALPHA, 1f).setDuration(100)
        val nameEditTextLayout =
            ObjectAnimator.ofFloat(binding.nameEditTextLayout, View.ALPHA, 1f).setDuration(100)
        val emailTextView =
            ObjectAnimator.ofFloat(binding.emailTextView, View.ALPHA, 1f).setDuration(100)
        val emailEditTextLayout =
            ObjectAnimator.ofFloat(binding.emailEditTextLayout, View.ALPHA, 1f).setDuration(100)
        val passwordTextView =
            ObjectAnimator.ofFloat(binding.passwordTextView, View.ALPHA, 1f).setDuration(100)
        val passwordEditTextLayout =
            ObjectAnimator.ofFloat(binding.passwordEditTextLayout, View.ALPHA, 1f).setDuration(100)
        val signup = ObjectAnimator.ofFloat(binding.signupButton, View.ALPHA, 1f).setDuration(100)


        AnimatorSet().apply {
            playSequentially(
                title,
                nameTextView,
                nameEditTextLayout,
                emailTextView,
                emailEditTextLayout,
                passwordTextView,
                passwordEditTextLayout,
                signup
            )
            startDelay = 100
        }.start()
    }
}