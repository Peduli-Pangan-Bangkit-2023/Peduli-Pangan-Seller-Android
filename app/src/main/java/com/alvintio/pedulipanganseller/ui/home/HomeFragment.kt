package com.alvintio.pedulipanganseller.ui.home

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.alvintio.pedulipanganseller.adapter.ProductAdapter
import com.alvintio.pedulipanganseller.databinding.FragmentHomeBinding
import com.alvintio.pedulipanganseller.ui.AddProductActivity
import com.google.android.material.floatingactionbutton.FloatingActionButton

class HomeFragment : Fragment() {
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

        val productList = listOf("Nasi Goreng", "Mie Goreng", "Bakso", "Sate")

        val priceList = listOf(
            Pair("Nasi Goreng", 15000),
            Pair("Mie Goreng", 15000),
            Pair("Bakso", 10000),
            Pair("Sate", 60000)
        )

        val productAdapter = ProductAdapter(productList, priceList.map { it.second })
        recyclerView.adapter = productAdapter

        val fab: FloatingActionButton = binding.fab
        fab.setOnClickListener {
            val intent = Intent(requireContext(), AddProductActivity::class.java)
            startActivity(intent)
        }

        return view
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
