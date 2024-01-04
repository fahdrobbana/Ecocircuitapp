package tn.esprit.ecocircuitapp.api

import okhttp3.MultipartBody
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.Part
import tn.esprit.ecocircuitapp.models.DonationCause

interface DonationFormApiServices {
    @GET("donationForm/")
    suspend fun getAllDonationForm(): ApiResponse<List<DonationCause>>
    @POST("donationForm/")
    @Multipart
    suspend fun addDonationForm(
        @Part("title") title: MultipartBody.Part,
        @Part("description") description: MultipartBody.Part,
        @Part("amount") amount: MultipartBody.Part,
        @Part image: MultipartBody.Part
    ): Response<DonationCause>
    data class ApiResponse<T>(val success: Boolean, val message: String?, val data: T?)

    object DonationFormApiServiceFactory {
        private const val BASE_URL = "http://192.168.1.113:9090/"

        fun create(): DonationFormApiServices {
            val retrofit = Retrofit.Builder()
                .baseUrl(BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build()

            return retrofit.create(DonationFormApiServices::class.java)
        }
    }
}
