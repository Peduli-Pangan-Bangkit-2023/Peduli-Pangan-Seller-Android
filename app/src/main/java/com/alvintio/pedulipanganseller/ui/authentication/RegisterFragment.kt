package com.alvintio.pedulipanganseller.ui.authentication

import android.os.Bundle
import android.transition.TransitionInflater
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import com.alvintio.pedulipanganseller.R
import com.alvintio.pedulipanganseller.databinding.FragmentRegisterBinding
import com.alvintio.pedulipanganseller.model.User
import com.alvintio.pedulipanganseller.viewmodel.AuthenticationViewModel
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.auth
import com.google.firebase.firestore.firestore

class RegisterFragment : Fragment() {

    private lateinit var binding: FragmentRegisterBinding
    private val viewModel: AuthenticationViewModel by activityViewModels()
    private lateinit var auth: FirebaseAuth

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
        binding = FragmentRegisterBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        auth = Firebase.auth

        binding.btnLogin.setOnClickListener {
            switchLoginFragment()
        }

        binding.btnAction.setOnClickListener {
            if (binding.edName.text?.length ?: 0 <= 0) {
                binding.edName.error = getString(R.string.field_empty_name)
                binding.edName.requestFocus()
            } else if (binding.edRegisterEmail.text?.length ?: 0 <= 0) {
                binding.edRegisterEmail.error = getString(R.string.field_empty_email)
                binding.edRegisterEmail.requestFocus()
            } else if (binding.edRegisterPassword.text?.length ?: 0 <= 0) {
                binding.edRegisterPassword.error = getString(R.string.field_empty_password)
                binding.edRegisterPassword.requestFocus()
            }
            else if (binding.edRegisterEmail.error?.length ?: 0 > 0) {
                binding.edRegisterEmail.requestFocus()
            } else if (binding.edRegisterPassword.error?.length ?: 0 > 0) {
                binding.edRegisterPassword.requestFocus()
            } else if (binding.edName.error?.length ?: 0 > 0) {
                binding.edName.requestFocus()
            }
            else {
                val name = binding.edName.text.toString()
                val email = binding.edRegisterEmail.text.toString()
                val password = binding.edRegisterPassword.text.toString()
                viewModel.register(name, email, password)
                registerWithEmailAndPassword(name, email, password)
            }
        }
    }

    private fun registerWithEmailAndPassword(name: String, email: String, password: String) {
        auth.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener(requireActivity()) { task ->
                if (task.isSuccessful) {
                    val user = task.result?.user
                    if (user != null) {
                        Log.d("Register", "User created successfully")
                        viewModel.register(binding.edName.text.toString(), email, password)
                        saveUserDataToFirestore(user.uid, name, email)

                        Toast.makeText(requireContext(), "Registrasi berhasil", Toast.LENGTH_SHORT).show()

                        switchLoginFragment()
                    }
                } else {
                    val error = task.exception?.message ?: getString(R.string.error)
                    Log.e("Register", "Registration failed: $error")

                    // Menangani kesalahan registrasi
                    if (error.contains("email address is already in use", true)) {
                        Toast.makeText(
                            requireContext(),
                            "Email telah terdaftar, mohon ganti menggunakan email lain!",
                            Toast.LENGTH_SHORT
                        ).show()
                    } else {
                        Toast.makeText(requireContext(), "Registrasi gagal: $error", Toast.LENGTH_SHORT).show()
                    }
                }
            }
    }

    private fun saveUserDataToFirestore(userId: String, name: String, email: String) {
        val db = Firebase.firestore
        val usersCollection = db.collection("users")

        val user = User(name, email, userId)

        usersCollection.document(userId)
            .set(user)
            .addOnSuccessListener {
                Log.d("Firestore", "Data telah tersimpan di Firestore!")
            }
            .addOnFailureListener { e ->
                Log.w("Firestore", "Data belum tersimpan di Firestore!", e)
            }
    }

    private fun switchLoginFragment() {
        parentFragmentManager.beginTransaction().apply {
            replace(R.id.container, LoginFragment(), LoginFragment::class.java.simpleName)
            commit()
        }
    }
}