package  tn.esprit.ecocircuitapp.ui.activities

import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.DatePicker
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import org.json.JSONObject
import android.widget.ProgressBar
import tn.esprit.ecocircuitapp.R
import tn.esprit.ecocircuitapp.viewModels.PaymentViewModel
import java.text.SimpleDateFormat
import java.util.*

class PaymentActivity : AppCompatActivity() {

    private lateinit var viewModel: PaymentViewModel

    private lateinit var amountEditText: EditText
    private lateinit var cardNumberEditText: EditText
    private lateinit var expiryDatePicker: DatePicker
    private lateinit var cvvEditText: EditText
    private lateinit var cardHolderNameEditText: EditText
    private lateinit var payNowButton: Button
    private lateinit var progressIndicator: ProgressBar


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_payment)

        viewModel = ViewModelProvider(this)[PaymentViewModel::class.java]

        amountEditText = findViewById(R.id.amountEditText)
        cardNumberEditText = findViewById(R.id.cardNumberEditText)
        expiryDatePicker = findViewById(R.id.expiryDatePicker)
        cvvEditText = findViewById(R.id.cvvEditText)
        cardHolderNameEditText = findViewById(R.id.cardHolderNameEditText)
        payNowButton = findViewById(R.id.payNowButton)
        progressIndicator = findViewById(R.id.progressIndicator)

        payNowButton.setOnClickListener {
            processPayment()
        }

        observeViewModel()
    }

    private fun observeViewModel() {
        viewModel.isLoading.observe(this, Observer { isLoading ->
            progressIndicator.visibility = if (isLoading) View.VISIBLE else View.GONE
        })

        viewModel.errorMessage.observe(this, Observer { errorMessage ->
            errorMessage?.let {
                // Handle error message
            }
        })
    }

    private fun processPayment() {

        val amount = amountEditText.text.toString()
        val cardNumber = cardNumberEditText.text.toString()
        val expiryDate = getFormattedDateFromDatePicker(expiryDatePicker)
        val cvv = cvvEditText.text.toString()
        val cardHolderName = cardHolderNameEditText.text.toString()

        // Validate inputs
        if (amount.isEmpty() || cardNumber.isEmpty() || expiryDate.isEmpty() || cvv.isEmpty() || cardHolderName.isEmpty()) {
            viewModel.errorMessage.value = "Please fill all fields correctly"
            return
        }
        if (cvv.length != 3) {
            viewModel.errorMessage.value = "CVV must be 3 digits"
            return
        }

        if (cardNumber.length != 16) {
            viewModel.errorMessage.value = "Card number must be 16 digits"
            return
        }

        // Convert inputs to appropriate types for your payment processing logic
        val amountDouble = amount.toDouble()

        // For credit card number, use it as a string
        val cardNumberStr = cardNumber

        val cvvInt = cvv.toInt()

        // Create JSON payload
        val payload = JSONObject().apply {
            put("amount", amountDouble)
            put("cardNumber", cardNumberStr)
            put("expiryDate", expiryDate)
            put("cvv", cvvInt)
            put("cardHolderName", cardHolderName)

            // Add other required fields to the payload
        }

        // Call ViewModel method to process payment
        viewModel.addPayment(payload)
    }

    private fun getFormattedDateFromDatePicker(datePicker: DatePicker): String {
        val day = datePicker.dayOfMonth
        val month = datePicker.month + 1
        val year = datePicker.year

        val calendar = Calendar.getInstance()
        calendar.set(year, month - 1, day)

        val dateFormat = SimpleDateFormat("MM/yy", Locale.getDefault())
        return dateFormat.format(calendar.time)
    }
}