package com.alvintio.pedulipanganseller.adapter

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.alvintio.pedulipanganseller.R
import com.alvintio.pedulipanganseller.model.Product
import com.alvintio.pedulipanganseller.ui.ProductDetailActivity
import com.bumptech.glide.Glide

class ProductAdapter(
    private var productList: List<Product>
) : RecyclerView.Adapter<ProductAdapter.ViewHolder>() {

    fun updateData(newProductList: List<Product>) {
        productList = newProductList
        notifyDataSetChanged()
    }

    override fun getItemCount(): Int {
        return productList.size
    }

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val productName: TextView = itemView.findViewById(R.id.tv_food)
        val productPrice: TextView = itemView.findViewById(R.id.tv_price)
        val attachmentImageView: ImageView = itemView.findViewById(R.id.iv_restaurant)

        init {
            itemView.setOnClickListener {
                val position = adapterPosition
                if (position != RecyclerView.NO_POSITION) {
                    val product = productList[position]
                    val context = itemView.context

                    val intent = Intent(context, ProductDetailActivity::class.java).apply {
                        putExtra(ProductDetailActivity.EXTRA_PRODUCT_NAME, product.name)
                        putExtra(ProductDetailActivity.EXTRA_PRODUCT_PRICE, product.price)
                        putExtra(ProductDetailActivity.EXTRA_PRODUCT_DESCRIPTION, product.detail)
                        putExtra(ProductDetailActivity.EXTRA_PRODUCT_IMAGE, product.attachment)
                    }

                    context.startActivity(intent)
                }
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.product_list, parent, false)
        return ViewHolder(view)
    }

    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val product = productList[position]

        holder.productName.text = product.name
        holder.productPrice.text = "Rp${String.format("%,.0f", product.price)}"
        Glide.with(holder.attachmentImageView.context)
            .load(product.attachment)
            .placeholder(R.drawable.ic_launcher_background)
            .error(R.drawable.ic_launcher_background)
            .into(holder.attachmentImageView) // Tambahkan baris ini

    }
}

