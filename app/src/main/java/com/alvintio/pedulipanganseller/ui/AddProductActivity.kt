package com.alvintio.pedulipanganseller.ui

import android.Manifest
import android.app.DatePickerDialog
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Matrix
import android.media.ExifInterface
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.view.View
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import com.alvintio.pedulipanganseller.R
import com.alvintio.pedulipanganseller.data.remote.ApiConfig
import com.alvintio.pedulipanganseller.databinding.ActivityAddProductBinding
import com.alvintio.pedulipanganseller.model.Product
import com.alvintio.pedulipanganseller.utils.Helper
import okhttp3.Call
import okhttp3.Callback
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.Response
import java.io.File
import java.io.FileOutputStream
import java.text.SimpleDateFormat
import java.util.Locale

class AddProductActivity : AppCompatActivity() {

    companion object {
        private val REQUIRED_CAMERA_PERMISS = arrayOf(Manifest.permission.CAMERA)
        private const val REQUEST_CODE_PERMISS = 101
    }

    private lateinit var binding: ActivityAddProductBinding

    private var pathImg: String = ""

    private var selectedDate: String = ""


    private val cameraLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
        if (it.resultCode == RESULT_OK) {
            val file = File(pathImg)
            file.let { image ->
                val bitmap = BitmapFactory.decodeFile(image.path)
                rotateImage(bitmap, pathImg).compress(
                    Bitmap.CompressFormat.JPEG,
                    100,
                    FileOutputStream(image)
                )
                binding.ivProductImage.setImageBitmap(rotateImage(bitmap, pathImg))
            }
        }
    }

    private val galleryLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
        if (it.resultCode == RESULT_OK) {
            val selectedImageUri: Uri? = it.data?.data
            selectedImageUri?.let {
                binding.ivProductImage.setImageURI(it)
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAddProductBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.apply {
            btnCamera.setOnClickListener {
                if (!checkImagePermission()) {
                    ActivityCompat.requestPermissions(
                        this@AddProductActivity,
                        REQUIRED_CAMERA_PERMISS,
                        REQUEST_CODE_PERMISS
                    )
                } else {
                    startCamera()
                }
            }

            btnGallery.setOnClickListener {
                openGallery()
            }
        }

        binding.apply {
            btnUpload.setOnClickListener {
                val productName = edName.text.toString()
                val productPrice = edPrice.text.toString()
                val productDescription = edDescription.text.toString()
                val latitude = edLatitude.text.toString()
                val longitude = edLongitude.text.toString()

                if (productName.isEmpty() || productPrice.isEmpty() || productDescription.isEmpty() || latitude.isEmpty() || longitude.isEmpty()) {
                    showToast("Semua field harus diisi")
                } else {
                    uploadProduct(productName, productPrice, productDescription, pathImg, selectedDate, latitude.toDouble(), longitude.toDouble())
                }
            }
        }
        Helper.setupFullScreen(this)
    }

    private fun uploadProduct(productName: String, productPrice: String, productDescription: String, imagePath: String, date: String, latitude: Double, longitude: Double) {
        val apiService = ApiConfig.getApiService()

        val imageFile = File(imagePath)
        val requestFile = imageFile.asRequestBody("multipart/form-data".toMediaTypeOrNull())
        val attachment = MultipartBody.Part.createFormData("attachment", imageFile.name, requestFile)

        val name = RequestBody.create("text/plain".toMediaTypeOrNull(), productName)
        val price = RequestBody.create("text/plain".toMediaTypeOrNull(), productPrice)
        val description = RequestBody.create("text/plain".toMediaTypeOrNull(), productDescription)
        val date = RequestBody.create("text/plain".toMediaTypeOrNull(), selectedDate)
        val latitude = RequestBody.create("text/plain".toMediaTypeOrNull(), latitude.toString())
        val longitude = RequestBody.create("text/plain".toMediaTypeOrNull(), longitude.toString())

        val call = apiService.uploadProduct(attachment, name, price, description, date, latitude, longitude)
        call.enqueue(object : retrofit2.Callback<Product> {
            override fun onResponse(
                call: retrofit2.Call<Product>,
                response: retrofit2.Response<Product>
            ) {
                if (response.isSuccessful) {
                    showToast("Produk berhasil diunggah")
                } else {
                    showToast("Gagal mengunggah produk. Kode: ${response.code()}")

                    val errorBody = response.errorBody()
                    if (errorBody != null) {
                        val errorMessage = errorBody.string()
                        showToast("Pesan Kesalahan: $errorMessage")
                    }
                }
            }

            override fun onFailure(call: retrofit2.Call<Product>, t: Throwable) {
                showToast("Gagal mengunggah produk. Silakan coba lagi.")
            }
        })
    }



        private fun checkImagePermission() = REQUIRED_CAMERA_PERMISS.all {
        ContextCompat.checkSelfPermission(baseContext, it) == PackageManager.PERMISSION_GRANTED
    }

    private fun startCamera() {
        val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        intent.resolveActivity(packageManager)
        val storageDir: File? = getExternalFilesDir(Environment.DIRECTORY_PICTURES)
        val customTempFile = File.createTempFile(
            SimpleDateFormat(
                "dd-MMM-yyyy",
                Locale.US
            ).format(System.currentTimeMillis()), ".jpg", storageDir
        )
        customTempFile.also {
            pathImg = it.absolutePath
            intent.putExtra(
                MediaStore.EXTRA_OUTPUT, FileProvider.getUriForFile(
                    this@AddProductActivity,
                    "com.alvintio.pedulipanganseller",
                    it
                )
            )
            cameraLauncher.launch(intent)
        }
    }

    private fun rotateImage(bitmap: Bitmap, path: String): Bitmap {
        val orientation = ExifInterface(path).getAttributeInt(
            ExifInterface.TAG_ORIENTATION,
            ExifInterface.ORIENTATION_UNDEFINED
        )
        val matrix = Matrix()
        when (orientation) {
            ExifInterface.ORIENTATION_ROTATE_90 -> matrix.setRotate(90f)
            ExifInterface.ORIENTATION_ROTATE_180 -> matrix.setRotate(180f)
        }

        return Bitmap.createBitmap(
            bitmap,
            0,
            0,
            bitmap.width,
            bitmap.height,
            matrix,
            true
        )
    }

    private fun openGallery() {
        val intent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
        galleryLauncher.launch(intent)
    }

    private fun showToast(message: String) {
        Toast.makeText(this@AddProductActivity, message, Toast.LENGTH_SHORT).show()
    }

    fun showDatePicker(view: View) {
        val datePicker = DatePickerDialog(
            this,
            { _, year, monthOfYear, dayOfMonth ->
                // Handle date selection
                selectedDate = String.format("%04d-%02d-%02d", year, monthOfYear + 1, dayOfMonth)
                binding.tvDateProducts.text = selectedDate
            },
            2023, 0, 1
        )
        datePicker.show()
    }


}