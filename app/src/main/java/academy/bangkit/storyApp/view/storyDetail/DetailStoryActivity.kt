package academy.bangkit.storyApp.view.storyDetail

import academy.bangkit.storyApp.R
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import academy.bangkit.storyApp.databinding.ActivityDetailStoryBinding
import academy.bangkit.storyApp.view.ViewModelFactory
import academy.bangkit.storyApp.view.extension.EnableFullscreen
import academy.bangkit.storyApp.view.extension.Image
import academy.bangkit.storyApp.view.extension.loadImageWithGlide
import academy.bangkit.storyApp.view.map.MapsActivity
import academy.bangkit.storyApp.view.viewImage.FullScreenImageActivity
import android.content.Intent
import android.graphics.Typeface
import android.text.Spannable
import android.text.SpannableStringBuilder
import android.text.style.StyleSpan
import android.view.View
import android.widget.Toast
import androidx.activity.viewModels
import androidx.core.app.ActivityOptionsCompat
import androidx.core.util.Pair
import com.google.android.gms.maps.model.LatLng
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

                val spannableStringBuilder = SpannableStringBuilder()

                spannableStringBuilder.append(story.name)
                spannableStringBuilder.setSpan(
                    StyleSpan(Typeface.BOLD),
                    0,
                    story.name.length,
                    Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
                )
                spannableStringBuilder.append(" ${story.description}")

                binding.tvDetailDescription.text = spannableStringBuilder
                binding.tvCreatedDate.text = calculateDateDifference(story.createdAt)
                if (story.lat != null && story.lon != null) {
                    binding.tvLocation.visibility = View.VISIBLE
                    binding.tvLocation.setOnClickListener {
                        val location = LatLng(story.lat, story.lon)
                        val intent = Intent(this@DetailStoryActivity, MapsActivity::class.java)
                        intent.putExtra(MapsActivity.EXTRA_LOCATION, location)
                        intent.putExtra(MapsActivity.EXTRA_NAME, story.name)
                        startActivity(intent)
                    }
                } else binding.tvLocation.visibility = View.GONE
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
        binding.downloadButton.visibility = if (isLoading) View.INVISIBLE else View.VISIBLE
        binding.shareButton.visibility = if (isLoading) View.INVISIBLE else View.VISIBLE
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