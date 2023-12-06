package academy.bangkit.storyApp.view.map

import academy.bangkit.storyApp.R
import academy.bangkit.storyApp.databinding.ActivityMapsBinding
import academy.bangkit.storyApp.view.ViewModelFactory
import academy.bangkit.storyApp.view.addStory.AddStoryActivity
import academy.bangkit.storyApp.view.extension.EnableFullscreen
import academy.bangkit.storyApp.view.listStory.MainActivity
import academy.bangkit.storyApp.view.storyDetail.DetailStoryActivity
import android.content.Intent
import android.content.res.Configuration
import android.os.Build
import android.os.Bundle
import android.view.View
import androidx.activity.viewModels
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.LatLngBounds
import com.google.android.gms.maps.model.MapStyleOptions
import com.google.android.gms.maps.model.MarkerOptions


class MapsActivity : AppCompatActivity(), OnMapReadyCallback {

    private val viewModel by viewModels<MapsActivityViewModel> {
        ViewModelFactory.getInstance(this)
    }

    private var userLocation: LatLng? = null
    private lateinit var mMap: GoogleMap
    private lateinit var binding: ActivityMapsBinding
    private val boundsBuilder = LatLngBounds.Builder()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMapsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        EnableFullscreen.setupView(window, supportActionBar)

        userLocation = if (Build.VERSION.SDK_INT >= 33) {
            intent.getParcelableExtra(EXTRA_LOCATION, LatLng::class.java)

        } else {
            @Suppress("DEPRECATION")
            intent.getParcelableExtra(EXTRA_LOCATION)
        }

        setupAppBar()

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        val mapFragment = supportFragmentManager
            .findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)
    }

    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap

        mMap.uiSettings.isZoomControlsEnabled = true
        mMap.uiSettings.isIndoorLevelPickerEnabled = true
        mMap.uiSettings.isCompassEnabled = true
        mMap.uiSettings.isMapToolbarEnabled = true

        val userName = intent.getStringExtra(EXTRA_NAME)

        if (userLocation != null) {
            val userLocation = userLocation as LatLng
            binding.topAppBar.apply {
                title = "Back to post"
                navigationIcon =
                    ContextCompat.getDrawable(this@MapsActivity, R.drawable.arrow_back)
                setNavigationOnClickListener { finish() }
            }

            mMap.addMarker(
                MarkerOptions()
                    .position(userLocation)
                    .title("$userName posted from here!")
            )
            mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(userLocation, 15f))


        } else {
            addManyMarker()
        }
        setMapStyle()
    }

    private fun setMapStyle() {

        when (resources.configuration.uiMode and Configuration.UI_MODE_NIGHT_MASK) {
            Configuration.UI_MODE_NIGHT_NO -> {
                mMap.setMapStyle(MapStyleOptions.loadRawResourceStyle(this, R.raw.map_style_light))
            }

            Configuration.UI_MODE_NIGHT_YES -> {
                mMap.setMapStyle(MapStyleOptions.loadRawResourceStyle(this, R.raw.map_style_dark))
            }
        }

    }

    private fun addManyMarker() {

        viewModel.getAllStories()
        viewModel.isLoading.observe(this) { isLoading ->
            showLoading(isLoading)
        }
        viewModel.storiesResponse.observe(this) {

            it.listStory.forEach { data ->
                if (data.lat != null && data.lon != null) {
                    val latLng = LatLng(data.lat, data.lon)
                    mMap.addMarker(
                        MarkerOptions()
                            .position(latLng)
                            .title(data.name)
                            .snippet(
                                if (data.description.codePointCount(
                                        0, data.description.length
                                    ) <= 30
                                ) {
                                    data.description
                                } else {
                                    "${data.description.substring(0, 20)}..."
                                }
                            )
                    )?.id.apply {
                        viewModel.idsMap[this as String] = data.id
                    }

                    mMap.setOnInfoWindowClickListener { marker ->
                        viewModel.idsMap.forEach { id ->
                            if (marker.id == id.key) {
                                val intentToDetail =
                                    Intent(this@MapsActivity, DetailStoryActivity::class.java)
                                        .putExtra(DetailStoryActivity.ID_STORY, id.value)
                                startActivity(intentToDetail)
                            }
                        }
                    }
                    boundsBuilder.include(latLng)
                }
            }
            val bounds: LatLngBounds = boundsBuilder.build()
            mMap.animateCamera(
                CameraUpdateFactory.newLatLngBounds(
                    bounds,
                    resources.displayMetrics.widthPixels,
                    resources.displayMetrics.heightPixels,
                    300
                )
            )
        }
    }

    private fun showLoading(isLoading: Boolean) {
        binding.loadingView.progressBar.visibility = if (isLoading) View.VISIBLE else View.GONE
    }

    private fun setupAppBar() {
        binding.topAppBar.setOnMenuItemClickListener {
            when (it.itemId) {
                R.id.list -> {
                    startActivity(Intent(this@MapsActivity, MainActivity::class.java))
                    true
                }

                R.id.addNewStory -> {
                    startActivity(Intent(this@MapsActivity, AddStoryActivity::class.java))
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

    companion object {
        const val EXTRA_LOCATION = "extra_location"
        const val EXTRA_NAME = "extra_name"
    }
}