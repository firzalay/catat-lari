package com.upn.catatlari.view.auth

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.upn.catatlari.data.local.AppDatabase
import com.upn.catatlari.databinding.FragmentRegisterBinding
import com.upn.catatlari.repository.UserRepository
import com.upn.catatlari.utils.setClickAnimation
import com.upn.catatlari.viewmodel.RegisterState
import com.upn.catatlari.viewmodel.RegisterViewModel

class RegisterFragment : Fragment() {

    private lateinit var binding: FragmentRegisterBinding

    private val viewModel: RegisterViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentRegisterBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        observeViewModel()

        // Tombol ke halaman Login
        binding.tvRegister.setClickAnimation {
            findNavController().navigate(
                RegisterFragmentDirections.actionRegisterFragmentToLoginFragment()
            )
        }

        // Tombol Register
        binding.btnRegister.setClickAnimation {
            val name     = binding.etName.text.toString()
            val email    = binding.etEmail.text.toString()
            val password = binding.etPassword.text.toString()
            viewModel.register(name, email, password)
        }
    }

    private fun observeViewModel() {
        viewModel.registerState.observe(viewLifecycleOwner) { state ->
            when (state) {
                is RegisterState.Loading -> {
                    binding.btnRegister.isEnabled = false
                    // tampilkan progress jika ada: binding.progressBar.visibility = View.VISIBLE
                }
                is RegisterState.Success -> {
                    Toast.makeText(requireContext(), "Registrasi berhasil!", Toast.LENGTH_SHORT).show()
                    findNavController().navigate(
                        RegisterFragmentDirections.actionRegisterFragmentToLoginFragment()
                    )
                }
                is RegisterState.Error -> {
                    binding.btnRegister.isEnabled = true
                    Toast.makeText(requireContext(), state.message, Toast.LENGTH_SHORT).show()
                }
            }
        }
    }
}