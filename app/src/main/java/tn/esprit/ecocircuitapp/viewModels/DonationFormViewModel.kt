package tn.esprit.ecocircuitapp.viewModels

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import tn.esprit.ecocircuitapp.models.DonationCause
import tn.esprit.ecocircuitapp.repositories.DonationFormRepository
import java.lang.Exception

class DonationFormViewModel(private val repository: DonationFormRepository) : ViewModel() {

    private val _donationCausesLiveData = MutableLiveData<List<DonationCause>>()
    val donationCausesLiveData: LiveData<List<DonationCause>> get() = _donationCausesLiveData

    fun getAllDonationForm() {
        viewModelScope.launch {
            try {
                val donationCauses = repository.getAllDonationForm()
                _donationCausesLiveData.postValue(donationCauses.sortedByDescending { it.createdAt ?: 0 })
            } catch (e: Exception) {
                handleException("Failed to get donation causes", e)
            }
        }
    }

    fun addDonationForm(donationCause: DonationCause, imageUri: String) {
        viewModelScope.launch {
            try {
                val updatedDonationCauses = repository.addDonationForm(donationCause, imageUri)
                _donationCausesLiveData.postValue(updatedDonationCauses.sortedByDescending { it.createdAt ?: 0 })
            } catch (e: Exception) {
                handleException("Failed to add donation cause", e)
            }
        }
    }

    private fun handleException(message: String, e: Exception) {
        Log.e("DonationFormViewModel", "$message: ${e.message}", e)
        // Handle the exception, e.g., show an error message to the user
    }
}