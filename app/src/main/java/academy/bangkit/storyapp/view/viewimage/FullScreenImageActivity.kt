package academy.bangkit.storyapp.view.viewimage

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import academy.bangkit.storyapp.databinding.ActivityFullScreenImageBinding
import academy.bangkit.storyapp.view.extension.Image
import academy.bangkit.storyapp.view.extension.EnableFullscreen
import academy.bangkit.storyapp.view.extension.loadImageWithGlide
import android.util.Log

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
        Log.d("imageUrl", data.photo)
        binding.fullScreenImageView.loadImageWithGlide(data.photo)
        binding.fullScreenImageView.setOnClickListener { finish() }
    }
}