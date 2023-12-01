package com.alvintio.pedulipanganseller.ui

import android.Manifest
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
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import com.alvintio.pedulipanganseller.R
import com.alvintio.pedulipanganseller.databinding.ActivityAddProductBinding
import com.alvintio.pedulipanganseller.utils.Helper
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

        Helper.setupFullScreen(this)
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
}