package com.alvintio.pedulipanganseller.ui

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.alvintio.pedulipanganseller.R
import com.alvintio.pedulipanganseller.databinding.ActivityProductDetailBinding
import com.alvintio.pedulipanganseller.utils.Helper
import com.bumptech.glide.Glide

class ProductDetailActivity : AppCompatActivity() {

    private lateinit var binding: ActivityProductDetailBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityProductDetailBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val productName = intent.getStringExtra(EXTRA_PRODUCT_NAME)
        val productPrice = intent.getDoubleExtra(EXTRA_PRODUCT_PRICE, 0.0)
        val productDescription = intent.getStringExtra(EXTRA_PRODUCT_DESCRIPTION)
        val productImage = intent.getStringExtra(EXTRA_PRODUCT_IMAGE)

        binding.tvProductNameDetail.text = productName
        binding.tvProductPriceDetail.text = "Rp${String.format("%,.0f", productPrice)}"
        binding.tvProductDescriptionDetail.text = productDescription

        Glide.with(this)
            .load(productImage)
            .placeholder(R.drawable.ic_launcher_background)
            .error(R.drawable.ic_launcher_background)
            .into(binding.ivProductDetail)

        Helper.setupFullScreen(this)
    }

    companion object {
        const val EXTRA_PRODUCT_NAME = "extra_product_name"
        const val EXTRA_PRODUCT_PRICE = "extra_product_price"
        const val EXTRA_PRODUCT_DESCRIPTION = "extra_product_description"
        const val EXTRA_PRODUCT_IMAGE = "extra_product_image"
    }
}
