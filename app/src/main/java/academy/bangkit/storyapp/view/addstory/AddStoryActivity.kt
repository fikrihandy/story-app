package academy.bangkit.storyapp.view.addstory

import academy.bangkit.storyapp.R
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import academy.bangkit.storyapp.databinding.ActivityAddStoryBinding
import academy.bangkit.storyapp.view.ViewModelFactory
import academy.bangkit.storyapp.view.extension.EnableFullscreen
import academy.bangkit.storyapp.view.liststory.MainActivity
import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.view.View
import android.widget.Toast
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.core.content.ContextCompat

class AddStoryActivity : AppCompatActivity() {
    private val viewModel by viewModels<AddStoryViewModel> {
        ViewModelFactory.getInstance(this)
    }
    private lateinit var binding: ActivityAddStoryBinding
    private var currentImageUri: Uri? = null

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

        binding.galleryButton.setOnClickListener { startGallery() }
        binding.buttonAdd.setOnClickListener { uploadImage() }
        binding.deleteButton.setOnClickListener { deleteImage() }
        binding.cameraButton.setOnClickListener { startCamera() }
    }

    private fun startCamera() {
        currentImageUri = getImageUri(this)
        launcherIntentCamera.launch(currentImageUri)
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
        if (currentImageUri == null) {
            showToast(getString(R.string.empty_image_warning))
            showLoading(false)
        }
        disableButton(true)
        currentImageUri?.let { uri ->
            val imageFile = uriToFile(uri, this).reduceFileImage()
            val description = binding.edAddDescription.text.toString()

            viewModel.uploadImage(imageFile, description).observe(this) { result ->
                if (result != null) {
                    when (result) {
                        is ResultState.Loading -> {
                            showLoading(true)
                        }

                        is ResultState.Success -> {
                            showToast(result.data.message)
                            showLoading(false)
                            startActivity(Intent(this, MainActivity::class.java))
                            finish()
                        }

                        is ResultState.Error -> {
                            showToast(result.error)
                            showLoading(false)
                            disableButton(false)
                        }
                    }
                }
            }
        } ?: {
            showToast(getString(R.string.empty_image_warning))
            showLoading(false)
        }

    }

    private fun disableButton(disableButton: Boolean) {
        with(binding) {
            buttonAdd.isEnabled = disableButton
            galleryButton.isEnabled = disableButton
            cameraButton.isEnabled = disableButton
            deleteButton.isEnabled = disableButton
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
            currentImageUri = uri
            showImage()
        } else {
            Toast.makeText(this@AddStoryActivity, "No media selected", Toast.LENGTH_SHORT).show()
        }
    }

    private fun showImage() {
        if (currentImageUri == null) {
            binding.previewImageView.setImageResource(R.drawable.ic_place_holder)
        } else {
            currentImageUri.let {
                binding.previewImageView.setImageURI(it)
            }
        }
    }

    private fun deleteImage() {
        if (currentImageUri == null) {
            showToast("You haven't selected an image yet :3")
        } else {
            currentImageUri = null
            showImage()
        }

    }

    companion object {
        private const val REQUIRED_PERMISSION = Manifest.permission.CAMERA
    }
}