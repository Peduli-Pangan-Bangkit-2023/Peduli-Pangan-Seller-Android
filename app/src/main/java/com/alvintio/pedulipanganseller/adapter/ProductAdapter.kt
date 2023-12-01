package com.alvintio.pedulipanganseller.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.alvintio.pedulipanganseller.R

class ProductAdapter(
    private var productList: List<String>,
    private var productPriceList: List<Int>
) : RecyclerView.Adapter<ProductAdapter.ViewHolder>() {

    fun updateData(newProductList: List<String>, newProductPriceList: List<Int>) {
        productList = newProductList
        productPriceList = newProductPriceList
        notifyDataSetChanged()
    }

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val productName: TextView = itemView.findViewById(R.id.tv_food)
        val productPrice: TextView = itemView.findViewById(R.id.tv_price)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.product_list, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val productName = productList[position]
        val productPrice = productPriceList[position]

        holder.productName.text = productName
        holder.productPrice.text = "Rp${productPrice}"
    }


    override fun getItemCount(): Int {
        return productList.size
    }
}


