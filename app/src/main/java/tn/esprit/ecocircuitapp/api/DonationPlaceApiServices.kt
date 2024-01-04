package tn.esprit.ecocircuitapp.api

import okhttp3.RequestBody
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import tn.esprit.ecocircuitapp.models.DonationPlace
import retrofit2.http.Multipart
import retrofit2.http.Part

interface DonationPlaceApiServices {

    @GET("donationPlace/")
    suspend fun getAllDonationPlaces(): Response<List<DonationPlace>>

//post request
@POST("donationPlace/addPlace")
suspend fun createDonationPlace(@Body place: DonationPlace): Response<DonationPlace>



}
