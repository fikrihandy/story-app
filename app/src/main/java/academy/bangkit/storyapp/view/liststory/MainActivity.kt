package academy.bangkit.storyapp.view.liststory

import academy.bangkit.storyapp.R
import academy.bangkit.storyapp.data.response.Story
import android.content.Intent
import android.os.Bundle
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import academy.bangkit.storyapp.databinding.ActivityMainBinding
import academy.bangkit.storyapp.view.ViewModelFactory
import academy.bangkit.storyapp.view.addstory.AddStoryActivity
import academy.bangkit.storyapp.view.authenticatiton.login.LoginActivity
import academy.bangkit.storyapp.view.extension.EnableFullscreen
import academy.bangkit.storyapp.view.extension.observeOnce
import android.view.View
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

    override fun onResume() {
        super.onResume()
        viewModel.getAllStories()
    }

    private fun setupAction() {

        viewModel.getAllStories()
        observeStoriesResponse()
        viewModel.isLoading.observe(this) { isLoading ->
            showLoading(isLoading)
        }

        binding.rvStories.setHasFixedSize(true)
        stories.addAll(getListHeroes())

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

                R.id.refresh -> {
                    viewModel.getAllStories()
                    observeStoriesResponse()
                    true
                }

                else -> false
            }

        }
    }

    private fun observeStoriesResponse() {
        viewModel.storiesResponse.observeOnce(this) { allStories ->
            Toast.makeText(this, allStories.message, Toast.LENGTH_LONG).show()
            if (allStories.listStory.isEmpty()) {
                binding.noData.visibility = View.VISIBLE
            }
            if (!allStories.error) {
                binding.noData.visibility = View.GONE
                setItemData(allStories.listStory)
            }
        }
    }

    private fun showLoading(isLoading: Boolean) {
        binding.loadingView.progressBar.visibility = if (isLoading) View.VISIBLE else View.GONE
    }

    private fun setItemData(stories: List<Story>) {
        val storyAdapter = StoryAdapter()
        storyAdapter.submitList(stories)
        binding.rvStories.adapter = storyAdapter
    }


    private fun getListHeroes(): ArrayList<Story> {
        return ArrayList()
    }
}