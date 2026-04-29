package com.upn.catatlari.view.auth

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.upn.catatlari.databinding.FragmentRegisterBinding
import com.upn.catatlari.utils.setClickAnimation

class RegisterFragment : Fragment() {

    private lateinit var binding: FragmentRegisterBinding

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        // Inflate the layout for this fragment
        binding = FragmentRegisterBinding.inflate(inflater, container, false)

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.tvRegister.setClickAnimation {
            findNavController().navigate(
                RegisterFragmentDirections.actionRegisterFragmentToLoginFragment()
            )
        }

        binding.btnRegister.setClickAnimation {
            findNavController().navigate(
                RegisterFragmentDirections.actionRegisterFragmentToLoginFragment()
            )
        }
    }


}