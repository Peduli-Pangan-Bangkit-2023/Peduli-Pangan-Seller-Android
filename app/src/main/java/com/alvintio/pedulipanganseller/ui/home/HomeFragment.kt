package com.alvintio.pedulipanganseller.ui.home

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.alvintio.pedulipanganseller.adapter.ProductAdapter
import com.alvintio.pedulipanganseller.data.remote.ApiConfig
import com.alvintio.pedulipanganseller.databinding.FragmentHomeBinding
import com.alvintio.pedulipanganseller.model.Product
import com.alvintio.pedulipanganseller.ui.AddProductActivity
import com.alvintio.pedulipanganseller.viewmodel.RestaurantViewModel
import com.google.android.material.floatingactionbutton.FloatingActionButton

class HomeFragment : Fragment() {
    private val restaurantViewModel: RestaurantViewModel by activityViewModels()

    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        val view = binding.root

        val recyclerView: RecyclerView = binding.recyclerView
        recyclerView.layoutManager = GridLayoutManager(requireContext(), 2)

        loadProducts()

        val fab: FloatingActionButton = binding.fab
        fab.setOnClickListener {
            val intent = Intent(requireContext(), AddProductActivity::class.java)
            startActivity(intent)
        }

        return view
    }

    private fun loadProducts() {
        val apiService = ApiConfig.getApiService()

        val call = apiService.getProducts()

        call.enqueue(object : retrofit2.Callback<List<Product>> {
            override fun onResponse(
                call: retrofit2.Call<List<Product>>,
                response: retrofit2.Response<List<Product>>
            ) {
                if (response.isSuccessful) {
                    val productList = response.body()
                    productList?.let {
                        val restaurantName = restaurantViewModel.restaurantName
                        val filteredProducts = it.filter { product -> product.name == restaurantName }
                        updateUI(filteredProducts)
                    }
                } else {
                    showToast("Gagal mengambil data produk. Kode: ${response.code()}")
                }
            }

            override fun onFailure(call: retrofit2.Call<List<Product>>, t: Throwable) {
                showToast("Gagal mengambil data produk. Silakan coba lagi.")
            }
        })
    }

    private fun updateUI(productList: List<Product>) {
        val productAdapter = ProductAdapter(productList)

        val recyclerView: RecyclerView = binding.recyclerView
        recyclerView.adapter = productAdapter
    }

    private fun showToast(message: String) {
        Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT).show()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
