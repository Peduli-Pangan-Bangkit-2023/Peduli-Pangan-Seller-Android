package com.alvintio.pedulipanganseller.ui.profile

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.ViewModelProvider
import com.alvintio.pedulipanganseller.databinding.FragmentProfileBinding
import com.alvintio.pedulipanganseller.ui.authentication.AuthenticationActivity
import com.alvintio.pedulipanganseller.viewmodel.RestaurantViewModel
import com.google.firebase.auth.FirebaseAuth

class ProfileFragment : Fragment() {
    private val restaurantViewModel: RestaurantViewModel by activityViewModels()

    private var _binding: FragmentProfileBinding? = null
    private val binding get() = _binding!!

    private lateinit var profileViewModel: ProfileViewModel
    private lateinit var auth: FirebaseAuth

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        profileViewModel = ViewModelProvider(this).get(ProfileViewModel::class.java)
        auth = FirebaseAuth.getInstance()

        _binding = FragmentProfileBinding.inflate(inflater, container, false)
        val root: View = binding.root

        binding.btnLogout.setOnClickListener {
            profileViewModel.logout()
            val intent = Intent(requireContext(), AuthenticationActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK
            startActivity(intent)
            requireActivity().finish()
        }

        return root
    }

    override fun onResume() {
        super.onResume()

        val currentUser = auth.currentUser
        if (currentUser != null) {
            profileViewModel.getUserData(currentUser.uid)
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        profileViewModel.userData.observe(viewLifecycleOwner) { userData ->
            binding.tvUserName.text = userData.name
            binding.tvUserEmail.text = userData.email
            restaurantViewModel.restaurantName = userData.name
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
