package com.upn.catatlari.view.run

import android.app.DatePickerDialog
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
import java.util.Calendar

class AddRunFragment : Fragment() {

    private lateinit var binding: FragmentAddRunBinding
    val runViewModel: RunViewModel by activityViewModels()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        binding = FragmentAddRunBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.btnSaveRun.setOnClickListener {
            val runTitle = binding.etRunTitle.text.toString()
            val runLocation = binding.etRunLocation.text.toString()
            val runDate = binding.etDate.text.toString()
            val runDuration = binding.etRunDuration.text.toString()
            val runDistance = binding.etRunDistance.text.toString()

            val runInput = Run(
                runTitle = runTitle,
                runLocation = runLocation,
                runDate = runDate,
                runDuration = runDuration.toInt(),
                runDistance = runDistance.toInt(),

            )

            runViewModel.addRun(runInput)
            findNavController().popBackStack()
        }

        binding.etDate.setOnClickListener {
            val calendar = Calendar.getInstance()
            val datePicker = DatePickerDialog(
                requireContext(),
                { _, year, month, dayOfMonth ->
                    val selectedDate = "$dayOfMonth/${month + 1}/$year"
                    binding.etDate.setText(selectedDate)
                },
                calendar.get(Calendar.YEAR),
                calendar.get(Calendar.MONTH),
                calendar.get(Calendar.DAY_OF_MONTH)
            )
            datePicker.show()
        }
    }
}