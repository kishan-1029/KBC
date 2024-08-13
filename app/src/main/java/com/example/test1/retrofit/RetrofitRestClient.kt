package com.example.test1.retrofit

import com.example.test1.interfaces.APIService
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.create
import java.util.concurrent.TimeUnit


object RetrofitRestClient {

    private const val TIME = 30

    private var httpClient : OkHttpClient? = null

        get() {
            field = OkHttpClient().newBuilder()
                .connectTimeout(TIME.toLong(),TimeUnit.SECONDS)
                .readTimeout(TIME.toLong(),TimeUnit.SECONDS)
                .writeTimeout(TIME.toLong(),TimeUnit.SECONDS)
                .build()
            return field
        }

    private val retrofit = Retrofit.Builder()
        .baseUrl(RestConstant.BASE_URL)
        .addConverterFactory(GsonConverterFactory.create())
        .client(httpClient!!)
        .build()
        .create(APIService::class.java)

    fun getInstance(): APIService {
        return retrofit
    }




}