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
import academy.bangkit.storyapp.view.authenticatiton.customview.CustomViewUtil
import academy.bangkit.storyapp.view.extension.EnableFullscreen
import androidx.activity.viewModels

class SignupActivity : AppCompatActivity() {
    private lateinit var binding: ActivitySignupBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySignupBinding.inflate(layoutInflater)
        setContentView(binding.root)

        CustomViewUtil.setupCustomView(
            binding.edRegisterEmail,
            binding.edRegisterPassword,
            binding.signupButton,
            binding.passwordEditTextLayout,
            binding.edRegisterName,
            loginPage = false
        )

        CustomViewUtil.setRegisterButtonEnable(
            binding.edRegisterName.text.toString(),
            binding.edRegisterEmail.text.toString(),
            binding.edRegisterPassword.text.toString(),
            binding.signupButton
        )

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