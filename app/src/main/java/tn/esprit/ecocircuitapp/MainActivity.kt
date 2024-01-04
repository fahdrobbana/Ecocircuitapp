package tn.esprit.ecocircuitapp

import android.content.Intent
import android.os.Bundle
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import tn.esprit.ecocircuitapp.databinding.ActivityMainBinding
import tn.esprit.ecocircuitapp.ui.activities.MapActivity
import tn.esprit.ecocircuitapp.ui.activities.PaymentActivity
import tn.esprit.ecocircuitapp.ui.activities.donationcauseform
import tn.esprit.ecocircuitapp.ui.activities.donationplaceform
import tn.esprit.ecocircuitapp.viewModels.DonationPlaceViewModel
import tn.esprit.ecocircuitapp.viewModels.PaymentViewModel


class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    private val viewModel1: DonationPlaceViewModel by viewModels()
    private val viewModel: PaymentViewModel by viewModels()


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.btnmap.setOnClickListener {
            val intent = Intent(this, MapActivity::class.java)
            startActivity(intent)
        }

        binding.btndonationplace.setOnClickListener {
            val intent = Intent(this, donationplaceform::class.java)
            startActivity(intent)
        }

        binding.btnpayment.setOnClickListener {
            val intent = Intent(this, PaymentActivity::class.java)
            startActivity(intent)
        }

        binding.btndonationcause.setOnClickListener {
            val intent = Intent(this, donationcauseform::class.java)
            startActivity(intent)
        }
    }
}
