package com.alvintio.pedulipanganseller.ui.authentication

import android.os.Bundle
import android.transition.TransitionInflater
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.ViewModelProvider
import com.alvintio.pedulipanganseller.R
import com.alvintio.pedulipanganseller.databinding.FragmentLoginBinding
import com.alvintio.pedulipanganseller.utils.Const
import com.alvintio.pedulipanganseller.utils.Helper
import com.alvintio.pedulipanganseller.utils.SettingPreferences
import com.alvintio.pedulipanganseller.utils.dataStore
import com.alvintio.pedulipanganseller.viewmodel.AuthenticationViewModel
import com.alvintio.pedulipanganseller.viewmodel.SettingViewModel
import com.alvintio.pedulipanganseller.viewmodel.ViewModelSettingFactory

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
        val pref = SettingPreferences.getInstance((activity as AuthenticationActivity).dataStore)
        val settingViewModel =
            ViewModelProvider(this, ViewModelSettingFactory(pref))[SettingViewModel::class.java]

        viewModel.let { vm ->
            vm.loginResult.observe(viewLifecycleOwner) { login ->
                // success login process triggered -> save preferences
                settingViewModel.setUserPreferences(
                    login.loginResult.token,
                    login.loginResult.userId,
                    login.loginResult.name,
                    viewModel.tempEmail.value ?: Const.preferenceDefaultValue
                )
            }
            vm.error.observe(viewLifecycleOwner) { error ->
                error?.let {
                    if (it.isNotEmpty()) {
                        Helper.showDialogInfo(requireContext(), it)
                    }
                }
            }
            vm.loading.observe(viewLifecycleOwner) { state ->
                binding.loading.root.visibility = state
            }
        }
        settingViewModel.getUserPreferences(Const.UserPreferences.UserToken.name)
            .observe(viewLifecycleOwner) { token ->
                // if token triggered change -> redirect to Main Activity
                if (token != Const.preferenceDefaultValue) (activity as AuthenticationActivity).routeToMainActivity()
            }
        binding.btnAction.setOnClickListener {
            /*
            *  NOTE REVIWER LALU :
            *  - untuk pengecekan logic tidak dilakukan di sini namun di file custom view
            *  - pengecekan disini -> jika input kosong tampilkan error field kosong
            *  - selain pengecekan field kosong -> tampilkan logic error dari custom view
            * */

            /* check if input is empty or not */
            if (binding.edLoginEmail.text?.length ?: 0 <= 0) {
                binding.edLoginEmail.error = getString(R.string.field_empty_email)
                binding.edLoginEmail.requestFocus()
            } else if (binding.edLoginPassword.text?.length ?: 0 <= 0) {
                binding.edLoginPassword.error = getString(R.string.field_empty_password)
                binding.edLoginPassword.requestFocus()
            }
            /* input not empty -> check contains error */
            else if (binding.edLoginEmail.error?.length ?: 0 > 0) {
                binding.edLoginEmail.requestFocus()
            } else if (binding.edLoginPassword.error?.length ?: 0 > 0) {
                binding.edLoginPassword.requestFocus()
            }
            /* not contain error */
            else {
                val email = binding.edLoginEmail.text.toString()
                val password = binding.edLoginPassword.text.toString()
                viewModel.login(email, password)
            }
        }
        binding.btnRegister.setOnClickListener {
            /* while view models contains error -> clear error before replace fragments (to hide dialog error)*/
            viewModel.error.postValue("")

            parentFragmentManager.beginTransaction().apply {
                replace(R.id.container, RegisterFragment(), RegisterFragment::class.java.simpleName)
                /* shared element transition to main activity */
                addSharedElement(binding.labelAuth, "auth")
                addSharedElement(binding.edLoginEmail, "email")
                addSharedElement(binding.edLoginPassword, "password")
                addSharedElement(binding.containerMisc, "misc")
                commit()
            }
        }
    }

    companion object {
        fun newInstance() = LoginFragment()
    }
}