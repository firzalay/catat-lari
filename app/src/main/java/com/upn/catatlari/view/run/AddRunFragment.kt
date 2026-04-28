package com.upn.catatlari.view.run

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import com.upn.catatlari.databinding.FragmentAddRunBinding
import com.upn.catatlari.model.Run
import com.upn.catatlari.viewmodel.RunViewModel

class AddRunFragment : Fragment() {

    private lateinit var binding: FragmentAddRunBinding
    val runViewModel : RunViewModel by activityViewModels()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        // Inflate the layout for this fragment
        binding = FragmentAddRunBinding.inflate(inflater, container, false)

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.btnSaveRun.setOnClickListener {
            val runDate = binding.etDate.text.toString()
            val runDuration = binding.etRunDuration.text.toString()
            val runDistance = binding.etRunDistance.text.toString()

            val runInput = Run(runDate = runDate, runDuration = runDuration.toInt(), runDistance = runDistance.toInt())

            runViewModel.addRun(runInput)
            findNavController().popBackStack()
        }
    }

}