package tn.esprit.ecocircuitapp.viewModels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.json.JSONObject
import java.io.OutputStreamWriter
import java.net.HttpURLConnection
import java.net.URL

class PaymentViewModel : ViewModel() {

    val isLoading = MutableLiveData<Boolean>().apply { value = false }
    val errorMessage = MutableLiveData<String>()

    fun addPayment(payload: JSONObject) {
        isLoading.value = true

        viewModelScope.launch {
            val result = withContext(Dispatchers.IO) {
                postRequest(URL("http://192.168.1.113:9090/api/payment/"), payload)
            }

            handleResult(result)

            isLoading.value = false
        }
    }

    private fun postRequest(url: URL, payload: JSONObject): MyResult<ByteArray> {
        return try {
            val connection = url.openConnection() as HttpURLConnection
            connection.requestMethod = "POST"
            connection.setRequestProperty("Content-Type", "application/json")
            connection.doOutput = true

            OutputStreamWriter(connection.outputStream).use {
                it.write(payload.toString())
            }

            val responseCode = connection.responseCode
            if (responseCode == HttpURLConnection.HTTP_OK) {
                val data = connection.inputStream.readBytes()
                MyResult.Success(data)
            } else {
                MyResult.Failure(Exception("HTTP error code: $responseCode"))
            }
        } catch (e: Exception) {
            MyResult.Failure(e)
        }
    }

    private fun handleResult(result: MyResult<ByteArray>) {
        when (result) {
            is MyResult.Success -> {
                // Process the successful response
                val responseString = result.value.decodeToString()
                println("Payment Added: $responseString")
                // Handle the response further as needed
            }
            is MyResult.Failure -> {
                // Handle the error
                val errorMessage = result.exception.message ?: "Unknown error"
                println("Error: $errorMessage")
                this.errorMessage.value = errorMessage
            }
        }
    }

    override fun onCleared() {
        super.onCleared()
        println("PaymentViewModel destroyed!")
    }

    sealed class MyResult<out T> {
        data class Success<out T>(val value: T) : MyResult<T>()
        data class Failure(val exception: Exception) : MyResult<Nothing>()
    }
}
