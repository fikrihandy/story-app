package academy.bangkit.storyApp.view.addStory

import academy.bangkit.storyApp.R
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import academy.bangkit.storyApp.databinding.ActivityAddStoryBinding
import academy.bangkit.storyApp.view.ViewModelFactory
import academy.bangkit.storyApp.view.extension.EnableFullscreen
import academy.bangkit.storyApp.view.extension.Image
import academy.bangkit.storyApp.view.listStory.MainActivity
import academy.bangkit.storyApp.view.viewImage.FullScreenImageActivity
import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Looper
import android.view.View
import android.widget.Toast
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.core.app.ActivityOptionsCompat
import androidx.core.content.ContextCompat
import androidx.core.util.Pair
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationResult
import com.google.android.gms.location.LocationServices


class AddStoryActivity : AppCompatActivity() {
    private val viewModel by viewModels<AddStoryViewModel> {
        ViewModelFactory.getInstance(this)
    }
    private lateinit var binding: ActivityAddStoryBinding
    private lateinit var fusedLocationClient: FusedLocationProviderClient

    private val requestCameraPermissionLauncher =
        registerForActivityResult(
            ActivityResultContracts.RequestPermission()
        ) { isGranted: Boolean ->
            if (isGranted) {
                Toast.makeText(this, "Permission request granted", Toast.LENGTH_LONG).show()
            } else {
                Toast.makeText(this, "Permission request denied", Toast.LENGTH_LONG).show()
            }
        }

    private fun cameraPermissionsGranted() =
        ContextCompat.checkSelfPermission(
            this,
            CAMERA_PERMISSION
        ) == PackageManager.PERMISSION_GRANTED


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAddStoryBinding.inflate(layoutInflater)
        setContentView(binding.root)
        EnableFullscreen.setupView(window, supportActionBar)

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)

        if (!cameraPermissionsGranted()) {
            requestCameraPermissionLauncher.launch(CAMERA_PERMISSION)
        }

        setupAppBar()

        binding.galleryButton.setOnClickListener { startGallery() }
        binding.buttonAdd.setOnClickListener { uploadImage() }
        binding.cameraButton.setOnClickListener { startCamera() }
        binding.previewImageView.setOnClickListener { fullScreen() }
        binding.checkboxLocation.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) {
                viewModel.setIsIncludeLoc(true)
                requestLocationUpdates()
            } else {
                viewModel.setIsIncludeLoc(false)
                stopLocationUpdates()
            }
        }
        showImage()
    }

    private val requestLocPermissionLauncher =
        registerForActivityResult(
            ActivityResultContracts.RequestMultiplePermissions()
        ) { permissions ->
            when {
                permissions[ACCESS_FINE_LOCATION_PERMISSION] ?: false -> {
                    requestLocationUpdates()
                }

                permissions[ACCESS_COARSE_LOCATION_PERMISSION] ?: false -> {
                    requestLocationUpdates()
                }

                else -> {
                    showToast("No location access granted")
                }
            }
        }

    private fun checkLocPermission(permission: String): Boolean {
        return ContextCompat.checkSelfPermission(
            this,
            permission
        ) == PackageManager.PERMISSION_GRANTED
    }

    private fun requestLocationUpdates() {
        if (checkLocPermission(ACCESS_FINE_LOCATION_PERMISSION) &&
            checkLocPermission(ACCESS_COARSE_LOCATION_PERMISSION)
        ) {
            val locationRequest = LocationRequest.create().apply {
                priority = LocationRequest.PRIORITY_HIGH_ACCURACY
                interval = 10000
                fastestInterval = 5000
            }

            fusedLocationClient.requestLocationUpdates(
                locationRequest,
                locationCallback,
                Looper.getMainLooper()
            )
        } else {
            requestLocPermissionLauncher.launch(
                arrayOf(
                    ACCESS_FINE_LOCATION_PERMISSION,
                    ACCESS_COARSE_LOCATION_PERMISSION
                )
            )
        }
    }

    private val locationCallback = object : LocationCallback() {
        override fun onLocationResult(locationResult: LocationResult) {
            locationResult.lastLocation.let { location ->
                viewModel.setUserLatLon(
                    location.latitude.toFloat(),
                    location.longitude.toFloat()
                )
            }
        }
    }

    private fun stopLocationUpdates() {
        fusedLocationClient.removeLocationUpdates(locationCallback)
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

        if (viewModel.getIsIncludeLoc()) {
            requestLocationUpdates()
        }

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
                        stopLocationUpdates()
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
        } ?: showToast(getString(R.string.empty_image_warning)).run {
            showLoading(false)
            enableButton(true)
        }

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
        val currentImageUri = viewModel.getImageUri()
        if (currentImageUri == null) {
            showToast("You haven't selected an image yet :3")
        } else {
            viewModel.setImageUri(null)
            showImage()
        }

    }

    companion object {
        private const val CAMERA_PERMISSION = Manifest.permission.CAMERA
        private const val ACCESS_FINE_LOCATION_PERMISSION = Manifest.permission.ACCESS_FINE_LOCATION
        private const val ACCESS_COARSE_LOCATION_PERMISSION =
            Manifest.permission.ACCESS_COARSE_LOCATION
    }
}