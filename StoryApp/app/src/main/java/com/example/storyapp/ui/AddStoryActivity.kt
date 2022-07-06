package com.example.storyapp.ui

import android.Manifest
import android.content.Intent
import android.content.Intent.ACTION_GET_CONTENT
import android.content.pm.PackageManager
import android.graphics.BitmapFactory
import android.location.Location
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.appcompat.app.AlertDialog
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.example.storyapp.R
import com.example.storyapp.databinding.ActivityAddStoryBinding
import com.example.storyapp.data.model.UserModel
import com.example.storyapp.data.result.ResultResponse
import com.example.storyapp.helper.Helper
import com.example.storyapp.viewmodel.AddStoryViewModel
import com.example.storyapp.viewmodel.ViewModelFactory
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import java.io.File

class AddStoryActivity : AppCompatActivity() {

    private lateinit var binding: ActivityAddStoryBinding
    private lateinit var user: UserModel
    private var getFile: File? = null
    private var location: Location? = null

    private val addStoryViewModel: AddStoryViewModel by viewModels {
        ViewModelFactory.getInstance(this)
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == REQUEST_CODE_PERMISSIONS) {
            if (!allPermissionsGranted()) {
                Toast.makeText(
                    this,
                    "Tidak mendapatkan permission.",
                    Toast.LENGTH_SHORT
                ).show()
                finish()
            }
        }
    }
    private fun allPermissionsGranted() = REQUIRED_PERMISSIONS.all {
        ContextCompat.checkSelfPermission(baseContext, it) == PackageManager.PERMISSION_GRANTED
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAddStoryBinding.inflate(layoutInflater)
        setContentView(binding.root)

        user = intent.getParcelableExtra(EXTRA_USER)!!

        if (!allPermissionsGranted()) {
            ActivityCompat.requestPermissions(
                this,
                REQUIRED_PERMISSIONS,
                REQUEST_CODE_PERMISSIONS
            )
        }

        initAction()
    }

    private fun initAction() {
        binding.btnCamera.setOnClickListener {
            startCamera()
        }

        binding.btnGallery.setOnClickListener {
            startGallery()
        }

        binding.btnUpload.setOnClickListener {
            upload()
        }
    }

    private fun startCamera() {
        val intent = Intent(this@AddStoryActivity, CameraActivity::class.java)
        launcherIntentCameraX.launch(intent)
    }

    private val launcherIntentCameraX = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {

        if (it.resultCode == CAMERA_X_RESULT) {
            val myFile = it.data?.getSerializableExtra("picture") as File
            val isBackCamera = it.data?.getBooleanExtra("isBackCamera", true) as Boolean

            getFile = myFile
            val result = Helper.rotateBitmap(
                BitmapFactory.decodeFile(myFile.path),
                isBackCamera
            )

            binding.ivPreview.setImageBitmap(result)
        }
    }

    private fun startGallery() {
        val intent = Intent()
        intent.action = ACTION_GET_CONTENT
        intent.type ="image/*"
        val chooser = Intent.createChooser(intent, "Choose a picture!")
        launcherIntentGallery.launch(chooser)
    }

    private val launcherIntentGallery = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
        if (result.resultCode == RESULT_OK) {
            val selectedImg: Uri = result.data?.data as Uri
            val myFile = Helper.uriToFile(selectedImg, this)

            getFile = myFile
            binding.ivPreview.setImageURI(selectedImg)
        }
    }

    private fun showAlertDialog(param: Boolean, message: String) {
        if (param) {
            AlertDialog.Builder(this).apply {
                setTitle(getString(R.string.information))
                setMessage(getString(R.string.upload_success))
                setPositiveButton(getString(R.string.continue_)) { _, _ ->
                    val intent = Intent(context, MainActivity::class.java)
                    intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK
                    startActivity(intent)
                    finish()
                }
                create()
                show()
            }
        } else {
            AlertDialog.Builder(this).apply {
                setTitle(getString(R.string.information))
                setMessage(getString(R.string.upload_failed) + ", $message")
                setPositiveButton(getString(R.string.continue_)) { _, _ ->
                    binding.pbLoading.visibility = View.GONE
                }
                create()
                show()
            }
        }
    }

    private fun upload() {
        if (getFile != null) {
            val file = Helper.reduceFileImage(getFile as File)

            val description = binding.edtStoryDesc.text
            if (description.isBlank()) {
                binding.edtStoryDesc.requestFocus()
                binding.edtStoryDesc.error = "Deskripsi tidak boleh kosong!"
            } else {
                val descMediaType = description.toString().toRequestBody("text/plain".toMediaType())
                val requestImageFile = file.asRequestBody("image/jpeg".toMediaTypeOrNull())
                val imageMultipart: MultipartBody.Part = MultipartBody.Part.createFormData(
                    "photo",
                    file.name,
                    requestImageFile
                )
                var lat: RequestBody? = null
                var lon: RequestBody? = null
                if (location != null) {
                    lat = location?.latitude.toString().toRequestBody("text/plain".toMediaType())
                    lon = location?.longitude.toString().toRequestBody("text/plain".toMediaType())
                }

                addStoryViewModel.  uploadImage(user.token, descMediaType, imageMultipart, lat, lon).observe(this) {
                    if (it != null) {
                        when (it) {
                            is ResultResponse.Loading -> {
                                binding.pbLoading.visibility = View.VISIBLE
                            }
                            is ResultResponse.Success -> {
                                binding.pbLoading.visibility = View.GONE
                                showAlertDialog(true, getString(R.string.upload_success))
                            }
                            is ResultResponse.Error -> {
                                binding.pbLoading.visibility = View.GONE
                                showAlertDialog(false, it.error)
                            }
                        }
                    }
                }
             }
        } else {
            Toast.makeText(this@AddStoryActivity,"Silakan masukkan berkas gambar terlebih dahulu.", Toast.LENGTH_SHORT).show()
        }
    }

    companion object {
        const val EXTRA_USER = "user"
        const val CAMERA_X_RESULT = 200
        private val REQUIRED_PERMISSIONS = arrayOf(Manifest.permission.CAMERA)
        private const val REQUEST_CODE_PERMISSIONS = 10
    }
}