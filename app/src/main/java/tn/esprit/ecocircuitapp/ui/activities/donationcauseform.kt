package tn.esprit.ecocircuitapp.ui.activities

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.widget.Button
import android.widget.ImageView
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.google.android.material.snackbar.Snackbar
import tn.esprit.ecocircuitapp.R
import tn.esprit.ecocircuitapp.databinding.ActivityDonationcauseformBinding
import tn.esprit.ecocircuitapp.models.DonationCause
import tn.esprit.ecocircuitapp.repositories.DonationFormRepository
import tn.esprit.ecocircuitapp.retrofit.Retrofit.donationFormApiServices
import tn.esprit.ecocircuitapp.viewModels.DonationFormViewModel


class donationcauseform : AppCompatActivity() {
    private lateinit var binding: ActivityDonationcauseformBinding
    private lateinit var imageView: ImageView
    private lateinit var imageUri: String
    private lateinit var viewModel: DonationFormViewModel
    private lateinit var getContent: ActivityResultLauncher<String>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDonationcauseformBinding.inflate(layoutInflater)
        setContentView(binding.root)
        btnSubmit()
        // Initialize imageUri before using it
        imageUri = ""

        imageView = binding.imageView
        val btnPickImage: Button = binding.btnPickImage

        // Rest of the code...
        // Initialize donationFormApiServices before using it

        val donationFormRepository = DonationFormRepository(donationFormApiServices)

        // Pass the repository instance to the ViewModel constructor
        viewModel = DonationFormViewModel(donationFormRepository)

        viewModel.donationCausesLiveData.observe(this) { donationcauses ->
            Log.d("Donationcauseform", "Donationcauses updated: ${donationcauses.size}")
            // You can update your UI or perform any actions based on the updated data here.
        }

        getContent = registerForActivityResult(ActivityResultContracts.GetContent()) { uri: Uri? ->
            binding.imageView.setImageURI(uri)
            imageUri = uri?.let { getRealPathFromURI(it) } ?: ""
            Log.d("Donationcauseform", "onCreate: $imageUri")
        }

        btnPickImage.setOnClickListener {
            pickImageFromGallery()
        }
    }

    private fun pickImageFromGallery() {
        val intent = Intent(Intent.ACTION_PICK)
        intent.type = "image/*"
        getContent.launch("image/*")
    }

    private fun getRealPathFromURI(uri: Uri): String? {
        val projection = arrayOf(MediaStore.Images.Media.DATA)
        val cursor = contentResolver.query(uri, projection, null, null, null)
        cursor?.use {
            val columnIndex = it.getColumnIndexOrThrow(MediaStore.Images.Media.DATA)
            it.moveToFirst()
            return it.getString(columnIndex)
        }
        return null
    }
    private fun btnSubmit() {
        val snackError = Snackbar.make(
            binding.root,  // Use the root view of your layout
            "Veuillez entrer vos coordonnées!",
            Snackbar.LENGTH_LONG
        )
            .setTextColor(ContextCompat.getColor(this, R.color.white))

        binding.btnSubmit.setOnClickListener {
            val title = binding.title.text.toString().trim()
            val description = binding.description.text.toString().trim()
            val image = imageUri
            val amount = binding.amount.text.toString().trim()

            if (isValidInput()) {
                val donationCause = DonationCause(
                    success = false,
                    message = null,
                    data = emptyList(),
                    title = title,
                    description = description,
                    image = image,
                    amount = amount.toFloatOrNull() ?: 0f,
                    createdAt = System.currentTimeMillis()
                )

                viewModel.addDonationForm(donationCause, imageUri)
            } else {
                snackError.show()
            }
        }
    }
    private fun isValidInput(): Boolean {
        val title = binding.title.text.toString().trim()
        val description = binding.description.text.toString().trim()
        val amount = binding.amount.text.toString().trim()

        return title.isNotEmpty() && description.isNotEmpty() && amount.isNotEmpty()
    }



}
