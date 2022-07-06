package com.example.storyapp.ui

import android.Manifest
import android.annotation.SuppressLint
import android.content.pm.PackageManager
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.core.content.ContextCompat
import com.example.storyapp.R
import com.example.storyapp.data.model.UserModel
import com.example.storyapp.data.result.ResultResponse
import com.example.storyapp.databinding.ActivityMapsBinding
import com.example.storyapp.helper.Helper
import com.example.storyapp.viewmodel.MapsViewModel
import com.example.storyapp.viewmodel.UserPreferencesModelFactory
import com.example.storyapp.viewmodel.ViewModelFactory
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.LatLngBounds
import com.google.android.gms.maps.model.MapStyleOptions
import com.google.android.gms.maps.model.MarkerOptions

class MapsActivity : AppCompatActivity(), OnMapReadyCallback {
    private lateinit var mMap: GoogleMap
    private lateinit var binding: ActivityMapsBinding
    private lateinit var user: UserModel

    private val viewModel: MapsViewModel by viewModels {
        ViewModelFactory.getInstance(this)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMapsBinding.inflate(layoutInflater)
        setContentView(binding.root)
        user = intent.getParcelableExtra(EXTRA_USER)!!

        val mapFragment = supportFragmentManager.findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)
    }

    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap
        mMap.setMapStyle(MapStyleOptions.loadRawResourceStyle(this, R.raw.map_style))

        mMap.uiSettings.isZoomControlsEnabled = true
        mMap.uiSettings.isIndoorLevelPickerEnabled = true
        mMap.uiSettings.isCompassEnabled = true
        mMap.uiSettings.isMapToolbarEnabled = true

        showTheData()
        getMyLocation()
    }

    private val requestPermissionLauncher = registerForActivityResult(ActivityResultContracts.RequestPermission()) { isGranted: Boolean ->
        if (isGranted) {
            getMyLocation()
        }
    }

    @SuppressLint("MissingPermission")
    private fun getMyLocation() {
        if (ContextCompat.checkSelfPermission(this.applicationContext, Manifest.permission.ACCESS_FINE_LOCATION,) == PackageManager.PERMISSION_GRANTED) {
            mMap.isMyLocationEnabled = true
        } else {
            requestPermissionLauncher.launch(Manifest.permission.ACCESS_FINE_LOCATION)
        }
    }

    private fun showTheData() {
        val boundsBuilder = LatLngBounds.Builder()
        viewModel.getStories(user.token).observe(this) {
            if (it != null) {
                when (it) {
                    is ResultResponse.Loading -> {
                        binding.progressBar.visibility = View.VISIBLE
                    }
                    is ResultResponse.Success -> {
                        binding.progressBar.visibility = View.GONE
                        it.data.forEachIndexed { _, element ->
                            val lastLatLng = LatLng(element.lat, element.lon)

                            mMap.addMarker(MarkerOptions().position(lastLatLng).title(element.id))
                            boundsBuilder.include(lastLatLng)
                            val bounds: LatLngBounds = boundsBuilder.build()
                            mMap.animateCamera(CameraUpdateFactory.newLatLngBounds(bounds, 64))
                        }
                    }
                    is ResultResponse.Error -> {
                        binding.progressBar.visibility = View.GONE
                        Helper.showToast(this, getString(R.string.err_occ))
                    }
                }
            }
        }
    }

    companion object {
        const val EXTRA_USER = "user"
    }
}