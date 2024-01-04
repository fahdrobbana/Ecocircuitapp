package tn.esprit.ecocircuitapp.retrofit

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import tn.esprit.ecocircuitapp.api.DonationFormApiServices
import tn.esprit.ecocircuitapp.api.DonationPlaceApiServices

object Retrofit {
    private const val BASE_URL = "http://192.168.1.113:9090/"

    private val retrofit by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }



    val donationFormApiServices: DonationFormApiServices by lazy {
        retrofit.create(DonationFormApiServices::class.java)
    }

    val donationPlaceApiServices: DonationPlaceApiServices by lazy {
        retrofit.create(DonationPlaceApiServices::class.java)
    }

}