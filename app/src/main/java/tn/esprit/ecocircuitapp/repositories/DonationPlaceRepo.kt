package tn.esprit.ecocircuitapp.repositories

import android.util.Log
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.RequestBody.Companion.toRequestBody
import retrofit2.Response
import tn.esprit.ecocircuitapp.models.DonationPlace
import tn.esprit.ecocircuitapp.retrofit.Retrofit
import tn.esprit.ecocircuitapp.utilities.ResponseHandler

class DonationPlaceRepo {
    private val retrofit = Retrofit.donationPlaceApiServices
    private val handler = ResponseHandler()

    suspend fun createDonationPlace(place: DonationPlace) {
        // Make the API call with the entire DonationPlace object
        val response = retrofit.createDonationPlace(place)

        if (response.isSuccessful) {
            // handle success
        } else {
            // handle failure
        }
    }

    suspend fun getAllDonationPlaces(): Response<List<DonationPlace>> {
        return retrofit.getAllDonationPlaces()
    }

}