package com.upn.catatlari.view.profile

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import com.upn.catatlari.R
import com.upn.catatlari.databinding.FragmentProfileBinding
import com.upn.catatlari.model.User
import com.upn.catatlari.view.activity.MainActivity
import com.upn.catatlari.viewmodel.ProfileViewModel
import com.upn.catatlari.viewmodel.RunViewModel
import java.io.File

class ProfileFragment : Fragment() {

    private lateinit var binding: FragmentProfileBinding
    private val runViewModel: RunViewModel by activityViewModels()
    private val profileViewModel: ProfileViewModel by activityViewModels()
    private var currentPhotoUri: Uri? = null
    private var tempPhotoFile: File? = null

    private val pickImageLauncher = registerForActivityResult(ActivityResultContracts.GetContent()) { uri: Uri? ->
        uri?.let { selectedUri ->
            currentPhotoUri = selectedUri
            saveProfilePhoto(selectedUri)
        }
    }

    private val cameraLauncher = registerForActivityResult(ActivityResultContracts.TakePicture()) { success ->
        if (success && tempPhotoFile != null) {
            currentPhotoUri = Uri.fromFile(tempPhotoFile)
            saveProfilePhoto(currentPhotoUri!!)
        }
    }

    private val permissionLauncher = registerForActivityResult(ActivityResultContracts.RequestPermission()) { isGranted ->
        if (isGranted) {
            openCamera()
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentProfileBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val user = (activity as MainActivity).user

        // Display user data
        updateUserInfo(user)

        // Observe run history to calculate stats
        runViewModel.runHistory.observe(viewLifecycleOwner) { runList ->
            val totalRuns = runList.size
            val totalDistance = runList.sumOf { it.runDistance }

            binding.tvTotalRuns.text = totalRuns.toString()
            binding.tvTotalDistance.text = totalDistance.toString()
        }

        // Change photo button click
        binding.btnChangePhoto.setOnClickListener {
            showPhotoPickerDialog()
        }

        // Edit Profile button click
        binding.btnEditProfile.setOnClickListener {
            user?.let { currentUser ->
                val action = ProfileFragmentDirections.actionProfileFragmentToEditProfileFragment(currentUser)
                findNavController().navigate(action)
            }
        }

        // Logout button click
        binding.btnLogout.setOnClickListener {
            requireActivity().finish()
        }
    }

    override fun onResume() {
        super.onResume()
        // Refresh user data when returning from EditProfileFragment
        val user = (activity as MainActivity).user
        updateUserInfo(user)
    }

    private fun updateUserInfo(user: User?) {
        binding.tvName.text = user?.name ?: "Unknown"
        binding.tvEmail.text = user?.email ?: "unknown@example.com"

        // Load profile photo
        user?.photoPath?.let { photoPath ->
            try {
                val photoFile = File(photoPath)
                if (photoFile.exists()) {
                    binding.ivProfile.setImageURI(Uri.fromFile(photoFile))
                }
            } catch (e: Exception) {
                // Keep placeholder if error loading photo
                binding.ivProfile.setImageResource(R.drawable.ic_profile_placeholder)
            }
        } ?: run {
            binding.ivProfile.setImageResource(R.drawable.ic_profile_placeholder)
        }
    }

    private fun showPhotoPickerDialog() {
        val options = arrayOf("Take Photo", "Choose from Gallery")
        android.app.AlertDialog.Builder(requireContext())
            .setTitle("Select Photo")
            .setItems(options) { _, which ->
                when (which) {
                    0 -> checkCameraPermissionAndOpen()
                    1 -> openGallery()
                }
            }
            .show()
    }

    private fun checkCameraPermissionAndOpen() {
        when {
            ContextCompat.checkSelfPermission(
                requireContext(),
                Manifest.permission.CAMERA
            ) == PackageManager.PERMISSION_GRANTED -> {
                openCamera()
            }
            shouldShowRequestPermissionRationale(Manifest.permission.CAMERA) -> {
                android.app.AlertDialog.Builder(requireContext())
                    .setTitle("Camera Permission Required")
                    .setMessage("Camera permission is needed to take a profile photo.")
                    .setPositiveButton("OK") { _, _ ->
                        permissionLauncher.launch(Manifest.permission.CAMERA)
                    }
                    .show()
            }
            else -> {
                permissionLauncher.launch(Manifest.permission.CAMERA)
            }
        }
    }

    private fun openCamera() {
        try {
            val photoFile = createImageFile()
            tempPhotoFile = photoFile
            val photoUri = FileProvider.getUriForFile(
                requireContext(),
                "${requireContext().packageName}.fileprovider",
                photoFile
            )
            cameraLauncher.launch(photoUri)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun openGallery() {
        pickImageLauncher.launch("image/*")
    }

    private fun createImageFile(): File {
        val storageDir = File(requireContext().getExternalFilesDir(null), "profile_photos")
        if (!storageDir.exists()) {
            storageDir.mkdirs()
        }
        return File(storageDir, "profile_${System.currentTimeMillis()}.jpg")
    }

    private fun saveProfilePhoto(uri: Uri) {
        try {
            val inputStream = requireContext().contentResolver.openInputStream(uri)
            val outputFile = createImageFile()
            inputStream?.use { input ->
                outputFile.outputStream().use { output ->
                    input.copyTo(output)
                }
            }

            val user = (activity as MainActivity).user
            user?.let { currentUser ->
                // Update user in MainActivity immediately
                val updatedUser = currentUser.copy(photoPath = outputFile.absolutePath)
                (requireActivity() as MainActivity).user = updatedUser

                // Update in database
                profileViewModel.updatePhotoPath(currentUser.id, outputFile.absolutePath)

                // Update UI
                binding.ivProfile.setImageURI(Uri.fromFile(outputFile))
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
}