package com.alvintio.pedulipanganseller.ui.authentication

import android.os.Bundle
import android.transition.TransitionInflater
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import com.alvintio.pedulipanganseller.R
import com.alvintio.pedulipanganseller.databinding.FragmentRegisterBinding
import com.alvintio.pedulipanganseller.utils.Helper
import com.alvintio.pedulipanganseller.viewmodel.AuthenticationViewModel

class RegisterFragment : Fragment() {

    private lateinit var binding: FragmentRegisterBinding
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
        binding = FragmentRegisterBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewModel.let { vm ->
            vm.registerResult.observe(viewLifecycleOwner) { register ->
                if (!register.error) {
                    val dialog = Helper.dialogInfoBuilder(
                        (activity as AuthenticationActivity),
                        getString(R.string.info_success_register)
                    )
                    val btnOk = dialog.findViewById<Button>(R.id.btn_ok)
                    btnOk.setOnClickListener {
                        dialog.dismiss()
                        switchLogin()
                    }
                    dialog.show()
                }
            }
            vm.error.observe(viewLifecycleOwner) { error ->
                if (error.isNotEmpty()) {
                    Helper.showDialogInfo(requireContext(), error)
                }
            }
            vm.loading.observe(viewLifecycleOwner) { state ->
                binding.loading.root.visibility = state
            }
        }
        binding.btnLogin.setOnClickListener {
            switchLogin()
        }
        binding.btnAction.setOnClickListener {
            /*
            *  NOTE REVIWER LALU :
            *  - untuk pengecekan logic tidak dilakukan di sini namun di file custom view
            *  - pengecekan disini -> jika input kosong tampilkan error field kosong
            *  - selain pengecekan field kosong -> tampilkan logic error dari custom view
            * */

            /* check if input is empty or not */
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
            /* input not empty -> check contains error */
            else if (binding.edRegisterEmail.error?.length ?: 0 > 0) {
                binding.edRegisterEmail.requestFocus()
            } else if (binding.edRegisterPassword.error?.length ?: 0 > 0) {
                binding.edRegisterPassword.requestFocus()
            } else if (binding.edName.error?.length ?: 0 > 0) {
                binding.edName.requestFocus()
            }
            /* not contain error */
            else {
                val name = binding.edName.text.toString()
                val email = binding.edRegisterEmail.text.toString()
                val password = binding.edRegisterPassword.text.toString()
                viewModel.register(name, email, password)
            }
        }
    }

    private fun switchLogin() {
        /* while view models contains error -> clear error before replace fragments (to hide dialog error)*/
        viewModel.error.postValue("")

        parentFragmentManager.beginTransaction().apply {
            replace(R.id.container, LoginFragment(), LoginFragment::class.java.simpleName)
            /* shared element transition to main activity */
            addSharedElement(binding.labelAuth, "auth")
            addSharedElement(binding.edRegisterEmail, "email")
            addSharedElement(binding.edRegisterPassword, "password")
            addSharedElement(binding.containerMisc, "misc")
            commit()
        }
    }
}