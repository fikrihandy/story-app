package academy.bangkit.storyApp.view.listStory

import academy.bangkit.storyApp.R
import android.content.Intent
import android.os.Bundle
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import academy.bangkit.storyApp.databinding.ActivityMainBinding
import academy.bangkit.storyApp.view.ViewModelFactory
import academy.bangkit.storyApp.view.addStory.AddStoryActivity
import academy.bangkit.storyApp.view.authentication.login.LoginActivity
import academy.bangkit.storyApp.view.extension.EnableFullscreen
import academy.bangkit.storyApp.view.map.MapsActivity
import androidx.appcompat.app.AlertDialog
import androidx.recyclerview.widget.LinearLayoutManager

class MainActivity : AppCompatActivity() {

    private val viewModel by viewModels<MainViewModel> {
        ViewModelFactory.getInstance(this)
    }
    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.rvStories.layoutManager = LinearLayoutManager(this)

        viewModel.getSession().observe(this) { user ->
            if (!user.isLogin) {
                startActivity(Intent(this, LoginActivity::class.java))
                finish()
            } else {
                EnableFullscreen.setupView(window, supportActionBar)
                setupAction()
            }
        }
    }

    private fun setupAction() {

        val adapter = StoryListAdapter()
        binding.rvStories.adapter = adapter.withLoadStateFooter(
            footer = LoadingStateAdapter {
                adapter.retry()
            }
        )

        viewModel.getStories(viewModel.getToken()).observe(this) {
            adapter.submitData(lifecycle, it)
        }

        binding.topAppBar.setOnMenuItemClickListener {
            when (it.itemId) {
                R.id.map -> {
                    startActivity(Intent(this@MainActivity, MapsActivity::class.java))
                    true
                }

                R.id.addNewStory -> {
                    startActivity(Intent(this@MainActivity, AddStoryActivity::class.java))
                    true
                }

                R.id.logout -> {
                    AlertDialog.Builder(this).apply {
                        setTitle("Logout?")
                        setMessage("Are you sure you want to logout?")
                        setCancelable(true)
                        setPositiveButton("Logout") { _, _ ->
                            viewModel.logout()
                            val intent = Intent(this@MainActivity, LoginActivity::class.java)
                            intent.flags =
                                Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK
                            startActivity(intent)
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
}