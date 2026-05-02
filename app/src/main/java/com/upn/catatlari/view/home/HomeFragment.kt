package com.upn.catatlari.view.home

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.snackbar.Snackbar
import com.upn.catatlari.R
import com.upn.catatlari.databinding.FragmentHomeBinding
import com.upn.catatlari.view.activity.MainActivity
import com.upn.catatlari.view.run.RunAdapter
import com.upn.catatlari.viewmodel.RunViewModel

class HomeFragment : Fragment() {

    private lateinit var binding: FragmentHomeBinding
    private val runViewModel: RunViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentHomeBinding.inflate(inflater, container, false)

        val user = (activity as MainActivity).user
        binding.welcomingTxt.text = "Halo, ${user?.name}"

        val runAdapter = RunAdapter(
            onItemClick = { run ->
                val action = HomeFragmentDirections.actionHomeFragmentToEditRunFragment(run)
                findNavController().navigate(action)
            },
            // ✅ BARU: Tampilkan dialog konfirmasi sebelum hapus
            onDeleteClick = { run ->
                MaterialAlertDialogBuilder(requireContext())
                    .setTitle("Hapus Catatan")
                    .setMessage("Apakah anda yakin untuk menghapus catatan ini?")
                    .setIcon(R.drawable.ic_delete_warning) // icon warning dari drawable yang akan kita buat
                    .setNegativeButton("Batal") { dialog, _ ->
                        dialog.dismiss()
                    }
                    .setPositiveButton("Hapus") { _, _ ->
                        runViewModel.deleteRun(run)
                    }
                    .show()
            }
        )

        binding.rvRunList.layoutManager = LinearLayoutManager(requireContext())
        binding.rvRunList.adapter = runAdapter

        runViewModel.runHistory.observe(viewLifecycleOwner) { runList ->
            runAdapter.setData(runList)
        }

        // ✅ BARU: Observer untuk status delete - tampilkan Snackbar setelah berhasil dihapus
        runViewModel.deleteStatus.observe(viewLifecycleOwner) { status ->
            if (status == null) return@observe
            if (status) {
                Snackbar.make(
                    binding.root,
                    "Catatan berhasil dihapus!",
                    Snackbar.LENGTH_SHORT
                ).show()
            } else {
                Snackbar.make(
                    binding.root,
                    "Gagal menghapus catatan. Coba lagi.",
                    Snackbar.LENGTH_SHORT
                ).show()
            }
            runViewModel.resetDeleteStatus()
        }

        return binding.root
    }
}