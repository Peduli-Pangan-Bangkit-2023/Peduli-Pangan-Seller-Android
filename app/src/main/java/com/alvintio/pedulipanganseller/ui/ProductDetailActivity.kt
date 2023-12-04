package com.alvintio.pedulipanganseller.ui

import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.alvintio.pedulipanganseller.R
import com.alvintio.pedulipanganseller.data.remote.ApiConfig
import com.alvintio.pedulipanganseller.data.remote.ApiService
import com.alvintio.pedulipanganseller.databinding.ActivityProductDetailBinding
import com.alvintio.pedulipanganseller.utils.Helper
import com.bumptech.glide.Glide
import retrofit2.Call
import retrofit2.Response

class ProductDetailActivity : AppCompatActivity() {

    private lateinit var binding: ActivityProductDetailBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityProductDetailBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val productId = intent.getStringExtra(EXTRA_PRODUCT_ID)
        val productName = intent.getStringExtra(EXTRA_PRODUCT_NAME)
        val productPrice = intent.getDoubleExtra(EXTRA_PRODUCT_PRICE, 0.0)
        val productDescription = intent.getStringExtra(EXTRA_PRODUCT_DESCRIPTION)
        val productImage = intent.getStringExtra(EXTRA_PRODUCT_IMAGE)

        binding.tvProductNameDetail.text = productName ?: ""
        binding.tvProductPriceDetail.text = "Rp${String.format("%,.0f", productPrice)}"
        binding.tvProductDescriptionDetail.text = productDescription ?: ""

        Glide.with(this)
            .load(productImage)
            .placeholder(R.drawable.ic_launcher_background)
            .error(R.drawable.ic_launcher_background)
            .into(binding.ivProductDetail)

        Helper.setupFullScreen(this)

        binding.btnDeleteProduct.setOnClickListener {
            if (productId != null) {
                deleteProduct(productId)
            } else {
                Toast.makeText(
                    this@ProductDetailActivity,
                    "Product ID is null. Unable to delete product.",
                    Toast.LENGTH_SHORT
                ).show()
            }
        }
    }

    private fun deleteProduct(productId: String) {
        val apiService = ApiConfig.getApiService()
        val call = apiService.deleteProduct(productId)

        call.enqueue(object : retrofit2.Callback<Void> {
            override fun onResponse(call: Call<Void>, response: Response<Void>) {
                if (response.isSuccessful) {
                    Toast.makeText(
                        this@ProductDetailActivity,
                        "Telah sukses menghapus data",
                        Toast.LENGTH_SHORT
                    ).show()

                    val backStackEntryCount = supportFragmentManager.backStackEntryCount
                    if (backStackEntryCount > 0) {
                        supportFragmentManager.popBackStack()
                    } else {
                        finish()
                    }

                } else {
                    Toast.makeText(
                        this@ProductDetailActivity,
                        "Gagal menghapus produk. Silakan coba lagi.",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }

            override fun onFailure(call: Call<Void>, t: Throwable) {
                Toast.makeText(
                    this@ProductDetailActivity,
                    "Terjadi kesalahan. Silakan coba lagi.",
                    Toast.LENGTH_SHORT
                ).show()
            }
        })
    }



    companion object {
        const val EXTRA_PRODUCT_ID = "extra_product_id"
        const val EXTRA_PRODUCT_NAME = "extra_product_name"
        const val EXTRA_PRODUCT_PRICE = "extra_product_price"
        const val EXTRA_PRODUCT_DESCRIPTION = "extra_product_description"
        const val EXTRA_PRODUCT_IMAGE = "extra_product_image"
    }
}
