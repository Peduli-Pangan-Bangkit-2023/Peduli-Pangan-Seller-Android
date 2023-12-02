package com.alvintio.pedulipanganseller.ui.authentication

import android.content.Intent
import android.os.Bundle
import android.transition.TransitionInflater
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import com.alvintio.pedulipanganseller.MainActivity
import com.alvintio.pedulipanganseller.R
import com.alvintio.pedulipanganseller.databinding.FragmentLoginBinding
import com.alvintio.pedulipanganseller.viewmodel.AuthenticationViewModel
import com.google.firebase.Firebase
import com.google.firebase.auth.auth

class LoginFragment : Fragment() {

    private lateinit var binding: FragmentLoginBinding
    private val viewModel: AuthenticationViewModel by activityViewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val animation = TransitionInflater.from(requireContext())
            .inflateTransition(android.R.transition.move)
        sharedElementEnterTransition = animation
        sharedElementReturnTransition = animation
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentLoginBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val auth = Firebase.auth

        if (auth.currentUser != null) {
            val intent = Intent(requireContext(), MainActivity::class.java)
            startActivity(intent)
            requireActivity().finish()
        }

        binding.btnAction.setOnClickListener {
            val email = binding.edLoginEmail.text.toString()
            val password = binding.edLoginPassword.text.toString()

            if (email.isEmpty()) {
                binding.edLoginEmail.error = getString(R.string.field_empty_email)
                binding.edLoginEmail.requestFocus()
            } else if (password.isEmpty()) {
                binding.edLoginPassword.error = getString(R.string.field_empty_password)
                binding.edLoginPassword.requestFocus()
            }
            else if (binding.edLoginEmail.error?.length ?: 0 > 0) {
                binding.edLoginEmail.requestFocus()
            } else if (binding.edLoginPassword.error?.length ?: 0 > 0) {
                binding.edLoginPassword.requestFocus()
            }
            else {
                viewModel.login(email, password)
            }
        }

        binding.btnRegister.setOnClickListener {
            parentFragmentManager.beginTransaction().apply {
                replace(R.id.container, RegisterFragment(), RegisterFragment::class.java.simpleName)
                addSharedElement(binding.labelAuth, "auth")
                addSharedElement(binding.edLoginEmail, "email")
                addSharedElement(binding.edLoginPassword, "password")
                addSharedElement(binding.containerMisc, "misc")
                commit()
            }
        }

        viewModel.loginState.observe(viewLifecycleOwner, { loginState ->
            when (loginState) {
                is AuthenticationViewModel.LoginState.Success -> {
                    Toast.makeText(requireContext(), "Login berhasil!", Toast.LENGTH_SHORT).show()

                    val intent = Intent(requireContext(), MainActivity::class.java)
                    startActivity(intent)
                    requireActivity().finish()
                }
                is AuthenticationViewModel.LoginState.Error -> {
                    Toast.makeText(requireContext(), loginState.message, Toast.LENGTH_SHORT).show()
                }
                else -> {
                }
            }
        })
    }
    companion object {
        fun newInstance() = LoginFragment()
    }
}