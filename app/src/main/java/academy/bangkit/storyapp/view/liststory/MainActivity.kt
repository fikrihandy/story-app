package academy.bangkit.storyapp.view.liststory

import academy.bangkit.storyapp.R
import academy.bangkit.storyapp.data.response.Story
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.view.WindowInsets
import android.view.WindowManager
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import academy.bangkit.storyapp.databinding.ActivityMainBinding
import academy.bangkit.storyapp.view.ViewModelFactory
import academy.bangkit.storyapp.view.addstory.AddStoryActivity
import academy.bangkit.storyapp.view.authenticatiton.login.LoginActivity
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.recyclerview.widget.LinearLayoutManager

class MainActivity : AppCompatActivity() {

    private val stories = ArrayList<Story>()

    private val viewModel by viewModels<MainViewModel> {
        ViewModelFactory.getInstance(this)
    }
    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        viewModel.getSession().observe(this) { user ->
            if (!user.isLogin) {
                startActivity(Intent(this, LoginActivity::class.java))
                finish()
            } else {
                setupView()
                setupAction()
//                playAnimation()
            }
        }

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
        Log.d("Stories", "Running")

        viewModel.getAllStories()

        viewModel.storiesResponse.observe(this) {
            Log.d("Stories", it.toString())
        }

        binding.rvStories.setHasFixedSize(true)
        stories.addAll(getListHeroes())
        showRecyclerList()


        binding.topAppBar.setOnMenuItemClickListener {
            when (it.itemId) {
                R.id.addNewStory -> {
                    val intent = Intent(this@MainActivity, AddStoryActivity::class.java)
                    startActivity(intent)
                    true
                }

                R.id.logout -> {
                    AlertDialog.Builder(this).apply {
                        setTitle("Logout?")
                        setMessage("Are you sure you want to logout?")
                        setCancelable(true)
                        setPositiveButton("Logout") { _, _ ->
                            viewModel.logout()
                            finish()
                        }
                        setNegativeButton("Cancel") { dialog, _ ->
                            dialog.dismiss()
                        }
                        create()
                        show()
                    }
                    true
                }

                else -> false
            }

        }
    }

    private fun showRecyclerList() {
        binding.rvStories.apply {
            layoutManager = LinearLayoutManager(this@MainActivity)
            val listStoryAdapter = ListStoryAdapter(stories)
            adapter = listStoryAdapter
            listStoryAdapter.setOnItemClickCallback(object : ListStoryAdapter.OnItemClickCallback {
                override fun onItemClicked(story: Story) {
                    showSelectedStory(story)
                }

                private fun showSelectedStory(story: Story) {
                    Toast.makeText(
                        this@MainActivity,
                        "U choose" + story.name,
                        Toast.LENGTH_SHORT
                    )
                        .show()
                }
            })
        }
    }

    private fun getListHeroes(): ArrayList<Story> {
        return ArrayList()
    }

//    private fun playAnimation() {
//        ObjectAnimator.ofFloat(binding.imageView, View.TRANSLATION_X, -30f, 30f).apply {
//            duration = 6000
//            repeatCount = ObjectAnimator.INFINITE
//            repeatMode = ObjectAnimator.REVERSE
//        }.start()
//
//        val name = ObjectAnimator.ofFloat(binding.nameTextView, View.ALPHA, 1f).setDuration(100)
//        val message =
//            ObjectAnimator.ofFloat(binding.messageTextView, View.ALPHA, 1f).setDuration(100)
//        val logout = ObjectAnimator.ofFloat(binding.actionLogout, View.ALPHA, 1f).setDuration(100)
//
//        AnimatorSet().apply {
//            playSequentially(name, message, logout)
//            startDelay = 100
//        }.start()
//    }
}