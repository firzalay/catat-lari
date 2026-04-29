package com.upn.catatlari.view.auth

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.upn.catatlari.databinding.FragmentLoginBinding
import com.upn.catatlari.utils.setClickAnimation
import com.upn.catatlari.view.activity.MainActivity
import com.upn.catatlari.viewmodel.LoginState
import com.upn.catatlari.viewmodel.LoginViewModel

class LoginFragment : Fragment() {

    private lateinit var binding: FragmentLoginBinding

    private val viewModel: LoginViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentLoginBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        observeViewModel()

        // Tombol ke halaman Register
        binding.tvRegister.setClickAnimation {
            findNavController().navigate(
                LoginFragmentDirections.actionLoginFragmentToRegisterFragment()
            )
        }

        // Tombol Login
        binding.btnLogin.setClickAnimation {
            val email = binding.etEmail.text.toString()
            val password = binding.etPassword.text.toString()
            viewModel.login(email, password)
        }
    }

    private fun observeViewModel() {
        viewModel.loginState.observe(viewLifecycleOwner) { state ->
            when (state) {
                is LoginState.Loading -> {
                    binding.btnLogin.isEnabled = false
                }

                is LoginState.Success -> {
                    Toast.makeText(
                        requireContext(),
                        "Selamat datang, ${state.user.name}!",
                        Toast.LENGTH_SHORT
                    ).show()

                    val intent = Intent(requireContext(), MainActivity::class.java)

                    intent.putExtra("user", state.user)

                    intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK

                    startActivity(intent)
                }

                is LoginState.Error -> {
                    binding.btnLogin.isEnabled = true
                    Toast.makeText(requireContext(), state.message, Toast.LENGTH_SHORT).show()
                }
            }
        }
    }
}