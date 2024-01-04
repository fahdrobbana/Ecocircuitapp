package tn.esprit.ecocircuitapp.viewModels

import android.content.ContentValues.TAG
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.liveData
import kotlinx.coroutines.Dispatchers
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.RequestBody.Companion.toRequestBody
import tn.esprit.ecocircuitapp.models.DonationPlace
import tn.esprit.ecocircuitapp.repositories.DonationPlaceRepo
import tn.esprit.ecocircuitapp.retrofit.Retrofit

class DonationPlaceViewModel : ViewModel() {
    private val _donationPlacesLiveData = MutableLiveData<List<DonationPlace>>()

    val dons: LiveData<List<DonationPlace>> get() = _donationPlacesLiveData
    val placeLiveData = MutableLiveData<DonationPlace?>()
    private val repo = DonationPlaceRepo()
    val don: MutableLiveData<List<DonationPlace>> get() = _donationPlacesLiveData


    fun getAllDonationPlaces() = liveData(Dispatchers.IO) {
        try {
            val response = Retrofit.donationPlaceApiServices.getAllDonationPlaces()
            if (response.isSuccessful) {
                emit(response.body())
            } else {
                Log.e(TAG, "Error: ${response.errorBody()?.string()}")
                emit(emptyList()) // or emit(null) if you prefer
            }
        } catch (e: Exception) {
            Log.e(TAG, "Exception: ${e.message}")
            emit(emptyList()) // or emit(null) if you prefer
        }
    }

    suspend fun createDonationPlace(don: DonationPlace) {
        // Make the API call with the entire DonationPlace object
        repo.createDonationPlace(don)
    }





}

