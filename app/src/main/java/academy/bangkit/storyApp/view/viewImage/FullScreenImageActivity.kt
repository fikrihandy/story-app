package academy.bangkit.storyApp.view.viewImage

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import academy.bangkit.storyApp.databinding.ActivityFullScreenImageBinding
import academy.bangkit.storyApp.view.extension.Image
import academy.bangkit.storyApp.view.extension.EnableFullscreen
import academy.bangkit.storyApp.view.extension.loadImageWithGlide

class FullScreenImageActivity : AppCompatActivity() {
    private lateinit var binding: ActivityFullScreenImageBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityFullScreenImageBinding.inflate(layoutInflater)
        setContentView(binding.root)
        EnableFullscreen.setupView(window, supportActionBar)
        setupData()
    }

    private fun setupData() {
        val data = intent.getParcelableExtra<Image>("Image") as Image
        binding.fullScreenImageView.loadImageWithGlide(data.photo)
        binding.fullScreenImageView.setOnClickListener { finish() }
    }
}