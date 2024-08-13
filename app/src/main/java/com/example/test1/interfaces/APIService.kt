package com.example.test1.interfaces

import com.example.test1.model.ResponseOfData
import com.example.test1.retrofit.RestConstant
import com.google.gson.JsonObject
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST

interface APIService {

    @POST(RestConstant.DEMO_JSON)
    fun getData(@Body body: JsonObject): Call<ResponseOfData>

}