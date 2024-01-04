package tn.esprit.ecocircuitapp.repositories

import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import tn.esprit.ecocircuitapp.api.DonationFormApiServices
import tn.esprit.ecocircuitapp.models.DonationCause
import java.io.File

class DonationFormRepository(private val donationFormApiServices: DonationFormApiServices) {

    suspend fun getAllDonationForm(): List<DonationCause> {
        try {
            val response = donationFormApiServices.getAllDonationForm()
            if (response.success) {
                return response.data ?: emptyList()
            } else {
                // Log or handle the server error
            }
        } catch (e: Exception) {
            // Log or handle the network error
        }
        return emptyList()
    }
    suspend fun addDonationForm(donationcause: DonationCause, imageUri: String): List<DonationCause> {
        try {
            val title = createPartFromString(donationcause.title)
            val description = createPartFromString(donationcause.description)
            val amount = createPartFromString(donationcause.amount.toString())

            val imageFile = File(imageUri)
            val imageRequestBody = imageFile.asRequestBody("image/*".toMediaTypeOrNull())
            val imagePart = MultipartBody.Part.createFormData("image", imageFile.name, imageRequestBody)

            val response = donationFormApiServices.addDonationForm(title, description, amount, imagePart)

            if (response.isSuccessful) {
                return response.body()?.data ?: emptyList()
            } else {
                // Log or handle the server error
            }
        } catch (e: Exception) {
            // Log or handle the network error
        }
        return emptyList()
    }

    private fun createPartFromString(string: String): MultipartBody.Part {
        val requestBody = string.toRequestBody("text/plain".toMediaTypeOrNull())
        return MultipartBody.Part.createFormData("text", string, requestBody)
    }
}