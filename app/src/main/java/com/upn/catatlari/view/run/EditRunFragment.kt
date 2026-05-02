package com.upn.catatlari.view.run

import android.app.DatePickerDialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.upn.catatlari.databinding.FragmentEditRunBinding
import com.upn.catatlari.model.Run
import com.upn.catatlari.viewmodel.RunViewModel
import java.util.Calendar

class EditRunFragment : Fragment() {

    private var _binding: FragmentEditRunBinding? = null
    private val binding get() = _binding!!
    private val viewModel: RunViewModel by activityViewModels()
    private val args: EditRunFragmentArgs by navArgs()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentEditRunBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val run = args.run

        binding.btnBack.setOnClickListener {
            findNavController().navigateUp()
        }
        binding.etRunTitle.setText(run.runTitle)
        binding.etRunLocation.setText(run.runLocation)
        binding.etDate.setText(run.runDate)
        binding.etRunDistance.setText(run.runDistance.toString())
        binding.etRunDuration.setText(run.runDuration.toString())

        binding.etDate.setOnClickListener {
            val calendar = Calendar.getInstance()
            DatePickerDialog(
                requireContext(),
                { _, year, month, day ->
                    binding.etDate.setText("$day/${month + 1}/$year")
                },
                calendar.get(Calendar.YEAR),
                calendar.get(Calendar.MONTH),
                calendar.get(Calendar.DAY_OF_MONTH)
            ).show()
        }

        binding.btnUpdateRun.setOnClickListener {
            val title = binding.etRunTitle.text.toString().trim()
            val location = binding.etRunLocation.text.toString().trim()
            val date = binding.etDate.text.toString().trim()
            val distance = binding.etRunDistance.text.toString().trim()
            val duration = binding.etRunDuration.text.toString().trim()

            if (title.isEmpty() || location.isEmpty() || date.isEmpty() ||
                distance.isEmpty() || duration.isEmpty()) {
                Toast.makeText(requireContext(), "Semua field harus diisi!", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val updatedRun = Run(
                id = run.id,
                runTitle = title,
                runLocation = location,
                runDate = date,
                runDistance = distance.toInt(),
                runDuration = duration.toInt()
            )

            viewModel.updateRun(updatedRun)
            Toast.makeText(requireContext(), "Data berhasil diupdate!", Toast.LENGTH_SHORT).show()
            findNavController().navigateUp()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}