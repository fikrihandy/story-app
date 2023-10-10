package academy.bangkit.storyapp.view.addstory

import academy.bangkit.storyapp.R
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import academy.bangkit.storyapp.databinding.ActivityAddStoryBinding
import academy.bangkit.storyapp.view.ViewModelFactory
import academy.bangkit.storyapp.view.extension.EnableFullscreen
import academy.bangkit.storyapp.view.extension.Image
import academy.bangkit.storyapp.view.liststory.MainActivity
import academy.bangkit.storyapp.view.viewimage.FullScreenImageActivity
import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.core.app.ActivityOptionsCompat
import androidx.core.content.ContextCompat
import androidx.core.util.Pair


class AddStoryActivity : AppCompatActivity() {
    private val viewModel by viewModels<AddStoryViewModel> {
        ViewModelFactory.getInstance(this)
    }
    private lateinit var binding: ActivityAddStoryBinding

    private val requestPermissionLauncher =
        registerForActivityResult(
            ActivityResultContracts.RequestPermission()
        ) { isGranted: Boolean ->
            if (isGranted) {
                Toast.makeText(this, "Permission request granted", Toast.LENGTH_LONG).show()
            } else {
                Toast.makeText(this, "Permission request denied", Toast.LENGTH_LONG).show()
            }
        }

    private fun allPermissionsGranted() =
        ContextCompat.checkSelfPermission(
            this,
            REQUIRED_PERMISSION
        ) == PackageManager.PERMISSION_GRANTED

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAddStoryBinding.inflate(layoutInflater)
        setContentView(binding.root)
        EnableFullscreen.setupView(window, supportActionBar)

        if (!allPermissionsGranted()) {
            requestPermissionLauncher.launch(REQUIRED_PERMISSION)
        }

        setupAppBar()

        binding.galleryButton.setOnClickListener { startGallery() }
        binding.buttonAdd.setOnClickListener { uploadImage() }
        binding.cameraButton.setOnClickListener { startCamera() }
        binding.previewImageView.setOnClickListener { fullScreen() }

        showImage()
    }

    private fun fullScreen() {
        val currentImageUri = viewModel.getImageUri()
        if (currentImageUri == null) {
            showToast("You haven't selected an image yet :3")
        } else {
            val image = Image(currentImageUri.toString())
            val intent = Intent(this, FullScreenImageActivity::class.java)
            intent.putExtra("Image", image)

            val optionsCompat: ActivityOptionsCompat =
                ActivityOptionsCompat.makeSceneTransitionAnimation(
                    this,
                    Pair(binding.previewImageView, "image")
                )
            startActivity(intent, optionsCompat.toBundle())
        }
    }

    private fun setupAppBar() {
        binding.topAppBar.setNavigationOnClickListener { finish() }
        binding.topAppBar.setOnMenuItemClickListener {
            when (it.itemId) {
                R.id.deleteImage -> {
                    deleteImage()
                    true
                }

                else -> false
            }
        }
    }

    private fun startCamera() {
        viewModel.setImageUri(getImageUri(this))
        launcherIntentCamera.launch(viewModel.getImageUri())
    }

    private val launcherIntentCamera = registerForActivityResult(
        ActivityResultContracts.TakePicture()
    ) { isSuccess ->
        if (isSuccess) {
            showImage()
        }
    }

    private fun uploadImage() {
        showLoading(true)
        val currentImageUri = viewModel.getImageUri()
        if (currentImageUri == null) {
            showToast(getString(R.string.empty_image_warning))
            showLoading(false)
        }
        enableButton(false)

        currentImageUri?.let { uri ->
            val imageFile = uriToFile(uri, this).reduceFileImage()
            val description = binding.edAddDescription.text.toString()

            viewModel.uploadImage(imageFile, description).observe(this) { result ->
                when (result) {
                    is ResultState.Loading -> {
                        enableButton(false)
                        showLoading(true)
                    }

                    is ResultState.Success -> {
                        showToast(result.data.message)
                        enableButton(false)
                        showLoading(false)
                        startActivity(Intent(this, MainActivity::class.java))
                        finish()
                    }

                    is ResultState.Error -> {
                        showToast(result.error)
                        enableButton(true)
                        showLoading(false)
                    }
                }
            }
        } ?: showToast(getString(R.string.empty_image_warning)).run { showLoading(false) }

    }

    private fun enableButton(enableButton: Boolean) {
        with(binding) {
            buttonAdd.isEnabled = enableButton
            galleryButton.isEnabled = enableButton
            cameraButton.isEnabled = enableButton
        }
    }

    private fun showLoading(isLoading: Boolean) {
        with(binding) {
            loadingView.progressBar.visibility = if (isLoading) View.VISIBLE else View.GONE
        }
    }

    private fun showToast(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }

    private fun startGallery() {
        launcherGallery.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly))
    }

    private val launcherGallery = registerForActivityResult(
        ActivityResultContracts.PickVisualMedia()
    ) { uri: Uri? ->
        if (uri != null) {
            viewModel.setImageUri(uri)
            showImage()
            Toast.makeText(
                this@AddStoryActivity,
                "Click on the image to view full screen",
                Toast.LENGTH_SHORT
            ).show()
        } else {
            Toast.makeText(this@AddStoryActivity, "No media selected", Toast.LENGTH_SHORT).show()
        }
    }

    private fun showImage() {
        val currentImageUri = viewModel.getImageUri()
        if (currentImageUri == null) {
            binding.previewImageView.setImageResource(R.drawable.ic_place_holder)
        } else {
            binding.previewImageView.setImageURI(currentImageUri)
        }
    }

    private fun deleteImage() {
        Log.d("DeletImage", "Delete Image running")
        val currentImageUri = viewModel.getImageUri()
        Log.d("DeletImage", currentImageUri.toString())
        if (currentImageUri == null) {
            Log.d("DeletImage", "Gagal")
            showToast("You haven't selected an image yet :3")
        } else {
            Log.d("DeletImage", "Berhasil")
            viewModel.setImageUri(null)
            showImage()
        }

    }

    companion object {
        private const val REQUIRED_PERMISSION = Manifest.permission.CAMERA
    }
}