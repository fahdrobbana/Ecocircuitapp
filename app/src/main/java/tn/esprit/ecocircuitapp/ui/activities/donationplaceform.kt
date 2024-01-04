package tn.esprit.ecocircuitapp.ui.activities

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.lifecycle.ViewModelProvider
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import tn.esprit.ecocircuitapp.R
import tn.esprit.ecocircuitapp.api.DonationPlaceApiServices
import tn.esprit.ecocircuitapp.databinding.ActivityDonationplaceformBinding
import tn.esprit.ecocircuitapp.models.DonationPlace
import tn.esprit.ecocircuitapp.viewModels.DonationPlaceViewModel


class donationplaceform : AppCompatActivity() {
    private lateinit var donationPlaceApiServices: DonationPlaceApiServices
    private lateinit var viewModel: DonationPlaceViewModel
    private lateinit var binding: ActivityDonationplaceformBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDonationplaceformBinding.inflate(layoutInflater)
        setContentView(binding.root)
        viewModel = ViewModelProvider(this).get(DonationPlaceViewModel::class.java)

        binding.btnSubmit.setOnClickListener {
            // Extract data from the UI
            val organizationName = binding.nomoraganisation.text.toString()
            val latitude = binding.Latitude.text.toString()
            val longitude = binding.Longitude.text.toString()
            val archived = false

            val donationPlace = DonationPlace(organizationName, longitude.toFloat(), latitude.toFloat(), archived)

            // Using a coroutine to call the suspend function
            CoroutineScope(Dispatchers.Main).launch {
                viewModel.createDonationPlace(donationPlace)
            }
        }

}
}