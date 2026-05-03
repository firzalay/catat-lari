package com.upn.catatlari.view.profile

import android.Manifest
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.upn.catatlari.databinding.FragmentEditProfileBinding
import com.upn.catatlari.utils.PasswordHelper
import com.upn.catatlari.viewmodel.ProfileViewModel
import java.io.File

class EditProfileFragment : Fragment() {

    private var _binding: FragmentEditProfileBinding? = null
    private val binding get() = _binding!!
    private val viewModel: ProfileViewModel by activityViewModels()
    private val args: EditProfileFragmentArgs by navArgs()
    private var currentPhotoPath: String? = null
    private var tempPhotoFile: File? = null

    private val pickImageLauncher = registerForActivityResult(ActivityResultContracts.GetContent()) { uri: Uri? ->
        uri?.let { selectedUri ->
            saveProfilePhoto(selectedUri)
        }
    }

    private val cameraLauncher = registerForActivityResult(ActivityResultContracts.TakePicture()) { success ->
        if (success && tempPhotoFile != null) {
            saveProfilePhoto(Uri.fromFile(tempPhotoFile))
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
        _binding = FragmentEditProfileBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val user = args.user

        // Pre-fill form with existing user data
        binding.etName.setText(user.name)
        binding.etEmail.setText(user.email)

        // Load existing profile photo
        user.photoPath?.let { photoPath ->
            currentPhotoPath = photoPath
            try {
                val photoFile = File(photoPath)
                if (photoFile.exists()) {
                    binding.ivProfilePreview.setImageURI(Uri.fromFile(photoFile))
                }
            } catch (e: Exception) {
                // Keep placeholder if error loading photo
            }
        }

        // Change photo button click
        binding.btnChangePhotoEdit.setOnClickListener {
            showPhotoPickerDialog()
        }

        // Back button click
        binding.btnBack.setOnClickListener {
            findNavController().navigateUp()
        }

        // Observe update status
        viewModel.updateStatus.observe(viewLifecycleOwner) { status ->
            if (status == null) return@observe
            if (status) {
                Toast.makeText(requireContext(), "Profile updated successfully!", Toast.LENGTH_SHORT).show()
                findNavController().navigateUp()
            } else {
                Toast.makeText(requireContext(), "Failed to update profile. Please try again.", Toast.LENGTH_SHORT).show()
            }
            viewModel.resetUpdateStatus()
        }

        // Save button click
        binding.btnSave.setOnClickListener {
            val name = binding.etName.text.toString().trim()
            val email = binding.etEmail.text.toString().trim()
            val currentPassword = binding.etCurrentPassword.text.toString().trim()
            val newPassword = binding.etNewPassword.text.toString().trim()
            val confirmPassword = binding.etConfirmPassword.text.toString().trim()

            // Validation
            if (name.isEmpty()) {
                Toast.makeText(requireContext(), "Name cannot be empty!", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            if (email.isEmpty()) {
                Toast.makeText(requireContext(), "Email cannot be empty!", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            if (!android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
                Toast.makeText(requireContext(), "Please enter a valid email!", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            if (currentPassword.isEmpty()) {
                Toast.makeText(requireContext(), "Please enter your current password to confirm changes!", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            // Verify current password
            if (!PasswordHelper.verify(currentPassword, user.password)) {
                Toast.makeText(requireContext(), "Current password is incorrect!", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            // Check if user wants to change password
            val finalPassword = if (newPassword.isNotEmpty() || confirmPassword.isNotEmpty()) {
                if (newPassword.isEmpty() || confirmPassword.isEmpty()) {
                    Toast.makeText(requireContext(), "Please fill both new password fields!", Toast.LENGTH_SHORT).show()
                    return@setOnClickListener
                }

                if (newPassword != confirmPassword) {
                    Toast.makeText(requireContext(), "New passwords do not match!", Toast.LENGTH_SHORT).show()
                    return@setOnClickListener
                }

                if (newPassword.length < 6) {
                    Toast.makeText(requireContext(), "New password must be at least 6 characters!", Toast.LENGTH_SHORT).show()
                    return@setOnClickListener
                }

                PasswordHelper.hash(newPassword)
            } else {
                user.password
            }

            // Update the user in MainActivity immediately for UI consistency
            val updatedUserData = user.copy(
                name = name,
                email = email,
                photoPath = currentPhotoPath
            )
            (requireActivity() as com.upn.catatlari.view.activity.MainActivity).user = updatedUserData

            // Update user in database
            viewModel.updateUser(user.id, name, email, finalPassword)

            // Update photo path separately if changed
            if (currentPhotoPath != user.photoPath) {
                viewModel.updatePhotoPath(user.id, currentPhotoPath)
            }
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

            currentPhotoPath = outputFile.absolutePath
            binding.ivProfilePreview.setImageURI(Uri.fromFile(outputFile))
        } catch (e: Exception) {
            e.printStackTrace()
            Toast.makeText(requireContext(), "Failed to save photo", Toast.LENGTH_SHORT).show()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
