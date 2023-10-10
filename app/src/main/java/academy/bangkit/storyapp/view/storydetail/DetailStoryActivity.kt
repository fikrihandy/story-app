package academy.bangkit.storyapp.view.storydetail

import academy.bangkit.storyapp.R
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import academy.bangkit.storyapp.databinding.ActivityDetailStoryBinding
import academy.bangkit.storyapp.view.ViewModelFactory
import academy.bangkit.storyapp.view.extension.EnableFullscreen
import academy.bangkit.storyapp.view.extension.Image
import academy.bangkit.storyapp.view.extension.loadImageWithGlide
import academy.bangkit.storyapp.view.viewimage.FullScreenImageActivity
import android.content.Intent
import android.view.View
import android.widget.Toast
import androidx.activity.viewModels
import androidx.core.app.ActivityOptionsCompat
import androidx.core.util.Pair
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.TimeZone


class DetailStoryActivity : AppCompatActivity() {

    private val viewModel by viewModels<DetailStoryViewModel> {
        ViewModelFactory.getInstance(this)
    }
    private lateinit var binding: ActivityDetailStoryBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDetailStoryBinding.inflate(layoutInflater)
        setContentView(binding.root)

        EnableFullscreen.setupView(window, supportActionBar)
        val id = intent.getStringExtra(ID_STORY) as String

        viewModel.getDetailStory(id)
        viewModel.detailStoryResponse.observe(this) {
            Toast.makeText(this, it.message, Toast.LENGTH_SHORT).show()
            if (it.error || it.story == null) {
                binding.ivDetailPhoto.setImageResource(R.drawable.ic_place_holder)
                binding.tvDetailDescription.text = it.message
                setupAppBar(id)
            }
            val story = it.story
            if (story != null) {
                setupAppBar(story.id, story.photoUrl)
                binding.ivDetailPhoto.loadImageWithGlide(story.photoUrl)
                binding.tvDetailName.text = story.name
                binding.tvDetailDescription.text = story.description
                binding.tvCreatedDate.text = calculateDateDifference(story.createdAt)
            }
        }
        viewModel.isLoading.observe(this) { isLoading ->
            showLoading(isLoading)
        }
    }

    private fun fullScreen(photoUrl: String) {
        val image = Image(photoUrl)
        val intent = Intent(this, FullScreenImageActivity::class.java)
        intent.putExtra("Image", image)

        val optionsCompat: ActivityOptionsCompat =
            ActivityOptionsCompat.makeSceneTransitionAnimation(
                this,
                Pair(binding.ivDetailPhoto, "image")
            )
        startActivity(intent, optionsCompat.toBundle())
    }

    private fun setupAppBar(id: String, photoUrl: String? = null) {

        binding.topAppBar.setNavigationOnClickListener {
            finish()
        }

        binding.topAppBar.setOnMenuItemClickListener {
            when (it.itemId) {
                R.id.fullscreen -> {
                    if (photoUrl != null) {
                        fullScreen(photoUrl)
                    } else {
                        Toast.makeText(this, "Story not found, please refresh", Toast.LENGTH_SHORT)
                            .show()
                    }
                    true
                }

                R.id.refresh -> {
                    viewModel.getDetailStory(id)
                    true
                }

                else -> false
            }
        }
    }

    private fun showLoading(isLoading: Boolean) {
        binding.loadingView.progressBar.visibility = if (isLoading) View.VISIBLE else View.GONE
    }

    private fun calculateDateDifference(createdDate: String): String {
        val formatter = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.getDefault())
        formatter.timeZone = TimeZone.getTimeZone("UTC")
        val date = formatter.parse(createdDate) as Date
        val pattern = "yyyy-MM-dd HH:mm:ss"
        val deviceTime = SimpleDateFormat(pattern, Locale.getDefault())
        val startDate =
            SimpleDateFormat(pattern, Locale.getDefault()).parse(deviceTime.format(date)) as Date
        val endDate =
            SimpleDateFormat(pattern, Locale.getDefault()).parse(deviceTime.format(Date())) as Date
        val diff = endDate.time - startDate.time
        val days = diff / (24 * 60 * 60 * 1000)
        val hours = (diff % (24 * 60 * 60 * 1000)) / (60 * 60 * 1000)
        val minutes = (diff % (60 * 60 * 1000)) / (60 * 1000)
        return when {
            days > 0 -> "$days days $hours hours $minutes minutes ago"
            hours > 0 -> "$hours hours $minutes minutes ago"
            minutes > 0 -> "$minutes minutes ago"
            else -> "less than a minute ago"
        }
    }

    companion object {
        const val ID_STORY = "extra_id"
    }
}